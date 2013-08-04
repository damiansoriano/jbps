package it.polimi.jbps.web.controller;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static it.polimi.jbps.utils.ObjectUtils.not;
import it.polimi.jbps.actions.Action;
import it.polimi.jbps.engine.Engine;
import it.polimi.jbps.entities.Context;
import it.polimi.jbps.entities.SimulationState;
import it.polimi.jbps.entities.SimulationTransition;
import it.polimi.jbps.exception.BPMNInvalidTransition;
import it.polimi.jbps.exception.InvalidPropertyAssignment;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Log4j
public class EngineController {
	
	private final Map<String, Engine> engines;
	private final Map<String, String> lanesDescriptions;
	private Map<String, SimulationState> enginesCurrentStates;
	private final Context context;
	
	public EngineController(Map<String, Engine> engines, Map<String, String> lanesDescriptions) {
		this.engines = engines;
		this.lanesDescriptions = lanesDescriptions;
		enginesCurrentStates = newHashMap();
		context = new Context();
	}
	
	@RequestMapping(value = "/")
    public String home(HttpServletRequest request, ModelMap model) {
		log.info(request);
		
		List<String> lanes = newLinkedList(engines.keySet());
		Collections.sort(lanes);
		model.addAttribute("lanes", lanes);
		
		Map<String, String> newLanesDescriptions = newHashMap();
		for (String lane : lanes) {
			if (lanesDescriptions.containsKey(lane)) {
				newLanesDescriptions.put(lane, lanesDescriptions.get(lane));
			} else {
				newLanesDescriptions.put(lane, lane);
			}
		}
		
		model.addAttribute("lanesDescriptions", newLanesDescriptions);
		
		return "home";
    }
	
	@RequestMapping(value = "/{lane}/startSimulation")
    public String startSimulation(@PathVariable String lane, HttpServletRequest request, ModelMap model) {
		log.info(request);
		
		Engine engine = engines.get(lane);
		SimulationState currentState = engine.startSimulation();
		enginesCurrentStates.put(lane, currentState);
		
		return String.format("redirect:/%s/currentState", lane);
	}
	
	@RequestMapping(value = "/{lane}/currentState")
    public String simulationState(@PathVariable String lane, @RequestParam(required=false) String errorMessage,
    		HttpServletRequest request, ModelMap model) {
		log.info(request);
		
		if (not(engines.containsKey(lane))) {
			return String.format("redirect:/%s/noDefinedLane", lane);
		}
		
		if (not(enginesCurrentStates.containsKey(lane))) {
			return String.format("redirect:/%s/startSimulation", lane);
		}
		
		Engine engine = engines.get(lane);
		SimulationState currentState = enginesCurrentStates.get(lane);
		
		if(engine.isEndState(currentState)) {
			return "successfullyFinished";
		}
		
		model.addAttribute("currentState", currentState);
		
		List<Action> actions = engine.getActionsWithPossibleAssignments(currentState);
		
		model.addAttribute("actions", actions);
		
		Map<SimulationTransition, SimulationState> nextStates = engine.getPossibleTransitions(currentState);
		if (nextStates.keySet().isEmpty()) {
			log.info("No transition was found in this state and this is not a terminal state. The BPMN Ontology may be badly defined.");
			log.info("Exiting simulation");
			return "noTransition";
		}
		
		model.addAttribute("transitions", nextStates);
		
		if (errorMessage != null) {
			model.addAttribute("errorMessage", errorMessage);
		}
		
		return "state";
    }
	
	@RequestMapping(value = "/{lane}/makeTransition", method = RequestMethod.POST)
    public String makeTransition(@PathVariable String lane, HttpServletRequest request, ModelMap model)
    		throws BPMNInvalidTransition {
		log.info(request);
		
		if (not(engines.containsKey(lane))) {
			return String.format("redirect:/%s/noDefinedLane", lane);
		}
		
		if (not(enginesCurrentStates.containsKey(lane))) {
			return String.format("redirect:/%s/startSimulation", lane);
		}
		
		Engine engine = engines.get(lane);
		SimulationState currentState = enginesCurrentStates.get(lane);
		
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
		
		String[] transitions = request.getParameterValues("transition");
		String transition = transitions[0];
		
		try {
			currentState = engine.makeTransition(currentState, assignments, transition, context);
		} catch (InvalidPropertyAssignment e) {
			log.error(e.getMessage());
			model.addAttribute("errorMessage", e.getMessage());
			return String.format("redirect:/%s/currentState", lane);
		}
		
		enginesCurrentStates.put(lane, currentState);
		
		return String.format("redirect:/%s/currentState", lane);
	}
}
