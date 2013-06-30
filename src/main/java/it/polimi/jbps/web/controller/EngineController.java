package it.polimi.jbps.web.controller;

import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.ObjectUtils.isNull;
import static it.polimi.jbps.utils.ObjectUtils.not;
import it.polimi.actions.Action;
import it.polimi.bpmn.simulation.SimulationState;
import it.polimi.bpmn.simulation.SimulationTransition;
import it.polimi.bpmn.simulation.Simulator;
import it.polimi.jbps.exception.BPMNInvalidTransition;
import it.polimi.jbps.exception.InvalidPropertyAssignment;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Log4j
public class EngineController {
	
	private final Simulator simulator;
	private SimulationState currentState;
	
	public EngineController(Simulator simulator) {
		this.simulator = simulator;
	}
	
	@RequestMapping(value = "/")
    public String home() {
		System.out.println("home");
		return "home";
    }
	
	@RequestMapping(value = "/startSimulation")
    public String startSimulation(HttpServletRequest request, ModelMap model) {
		log.info(request);
		System.out.println("############################## startSimulation");
		System.out.println("currentState: " + currentState);
		currentState = simulator.startSimulation();
		return "redirect:/currentState";
	}
	
	@RequestMapping(value = "/currentState")
    public String simulationState(HttpServletRequest request, ModelMap model) {
		log.info(request);
		
		if (isNull(currentState)) {
			return "redirect:/startSimulation";
		}
		
		if(simulator.isEndState(currentState)) {
			return "successfullyFinished";
		}
		
		model.addAttribute("currentStateURI", currentState.getStateURI());
		
		List<Action> actions = simulator.getActions(currentState);
		simulator.setPossibleAssignments(actions);
		
		model.addAttribute("actions", actions);
		
		Map<SimulationTransition, SimulationState> nextStates = simulator.getNextStates(currentState);
		if (nextStates.keySet().isEmpty()) {
			log.info("No transition was found in this state and this is not a terminal state. The BPMN Ontology may be badly defined.");
			log.info("Exiting simulation");
			return "noTransition";
		}
		
		model.addAttribute("transitions", nextStates);
		
		return "state";
    }
	
	@RequestMapping(value = "/makeTransition", method = RequestMethod.POST)
    public String makeTransition(HttpServletRequest request, ModelMap model) throws InvalidPropertyAssignment, BPMNInvalidTransition {
		log.info(request);
		
		log.info("currentState: " + currentState);
		
		Map<String, String> assignments = newHashMap();
		
		@SuppressWarnings("unchecked")
		Enumeration<String> parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String key = parameterNames.nextElement();
			
			if(not(key.equals("transition"))) {
				String[] parameterValues = request.getParameterValues(key);
				assignments.put(key, parameterValues[0]);
			}
		}
		log.info("assignments: " + assignments);
		
		String[] transitions = request.getParameterValues("transition");
		String transition = transitions[0];
		log.info("transition: " + transition);
		
		simulator.applyActions(currentState, assignments);
		currentState = simulator.move(currentState, transition);
		
		return "redirect:/currentState";
	}
}
