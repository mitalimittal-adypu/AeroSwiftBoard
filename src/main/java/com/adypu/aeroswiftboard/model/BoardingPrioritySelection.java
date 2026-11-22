package com.adypu.aeroswiftboard.model;

/**
 * Represents the optional boarding-priority preferences selected in the UI.
 *
 * @param seniorCitizen whether senior-citizen priority is selected
 * @param veteran whether veteran priority is selected
 */
public record BoardingPrioritySelection(Boolean seniorCitizen, Boolean veteran) {
}
