/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.validator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Contains methods for testing {@link EncounterValidator#validate(Object, Errors)}
 */
public class EncounterValidatorTest extends BaseContextSensitiveTest {
	
	/**
	 * @see EncounterValidator#validate(Object,Errors)
	 */
	@Test
	public void validate_shouldFailIfThePatientsForTheVisitAndTheEncounterDontMatch() {
		Encounter encounter = new Encounter();
		encounter.setPatient(new Patient(2));
		Visit visit = new Visit();
		visit.setPatient(new Patient(3));
		encounter.setVisit(visit);
		Errors errors = new BindException(encounter, "encounter");
		new EncounterValidator().validate(encounter, errors);
		Assert.assertEquals("Encounter.visit.patients.dontMatch", errors.getFieldError("visit").getCode());
	}
	
	/**
	 * @see EncounterValidator#validate(Object,Errors)
	 */
	@Test
	public void validate_shouldFailIfTheVisitHasNoPatient() {
		Encounter encounter = new Encounter();
		encounter.setPatient(new Patient(2));
		Visit visit = new Visit();
		encounter.setVisit(visit);
		Errors errors = new BindException(encounter, "encounter");
		new EncounterValidator().validate(encounter, errors);
		Assert.assertEquals("Encounter.visit.patients.dontMatch", errors.getFieldError("visit").getCode());
	}
	
	/**
	 * @see EncounterValidator#validate(Object,Errors)
	 */
	@Test
	public void validate_shouldFailIfPatientIsNotSet() {
		Encounter encounter = new Encounter();
		Errors errors = new BindException(encounter, "encounter");
		new EncounterValidator().validate(encounter, errors);
		Assert.assertTrue(errors.hasFieldErrors("patient"));
	}
	
	/**
	 * @see EncounterValidator#validate(Object,Errors)
	 */
	@Test
	public void validate_shouldFailIfEncounterDateTimeIsBeforeVisitStartDateTime() {
		Visit visit = Context.getVisitService().getVisit(1);
		
		Encounter encounter = Context.getEncounterService().getEncounter(3);
		visit.setPatient(encounter.getPatient());
		encounter.setVisit(visit);
		
		//Set encounter dateTime to before the visit startDateTime.
		Date date = new Date(visit.getStartDatetime().getTime() - 1);
		encounter.setEncounterDatetime(date);
		
		Errors errors = new BindException(encounter, "encounter");
		new EncounterValidator().validate(encounter, errors);
		Assert.assertEquals(true, errors.hasFieldErrors("encounterDatetime"));
	}
	
	/**
	 * @see EncounterValidator#validate(Object,Errors)
	 */
	@Test
	public void validate_shouldFailIfEncounterDateTimeIsAfterVisitStopDateTime() {
		Visit visit = Context.getVisitService().getVisit(1);
		
		Encounter encounter = Context.getEncounterService().getEncounter(3);
		visit.setPatient(encounter.getPatient());
		encounter.setVisit(visit);
		
		//Set encounter dateTime to after the visit stopDateTime.
		visit.setStopDatetime(new Date());
		Date date = new Date(visit.getStopDatetime().getTime() + 1);
		encounter.setEncounterDatetime(date);
		
		Errors errors = new BindException(encounter, "encounter");
		new EncounterValidator().validate(encounter, errors);
		Assert.assertEquals(true, errors.hasFieldErrors("encounterDatetime"));
	}
	
	/**
	 * @see EncounterValidator#validate(Object,Errors)
	 */
	@Test
	public void validate_shouldFailIfEncounterDateTimeIsAfterCurrentDateTime() {
		
		Encounter encounter = Context.getEncounterService().getEncounter(3);
		
		//Set encounter dateTime after the current dateTime.
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrowDate = calendar.getTime();
		encounter.setEncounterDatetime(tomorrowDate);
		
		Errors errors = new BindException(encounter, "encounter");
		new EncounterValidator().validate(encounter, errors);
		Assert.assertEquals(true, errors.hasFieldErrors("encounterDatetime"));
	}
	
	/**
	 * @see EncounterValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate_shouldFailIfEncounterDateTimeIsNotSet() {
		
		Encounter encounter = new Encounter();
		
		Errors errors = new BindException(encounter, "encounter");
		new EncounterValidator().validate(encounter, errors);
		Assert.assertTrue(errors.hasFieldErrors("encounterDatetime"));
	}
	
	/**
	 * @see EncounterValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate_shouldFailIfEncounterTypeIsNotSet() {
		Encounter encounter = new Encounter();
		Errors errors = new BindException(encounter, "encounter");
		new EncounterValidator().validate(encounter, errors);
		Assert.assertTrue(errors.hasFieldErrors("encounterType"));
	}
	
	/**
	 * @see EncounterValidator#validate(Object,Errors)
	 */
	@Test
	public void validate_shouldPassValidationIfFieldLengthsAreCorrect() {
		Encounter encounter = new Encounter();
		encounter.setEncounterType(new EncounterType());
		encounter.setPatient(new Patient());
		encounter.setEncounterDatetime(new Date());
		encounter.setVoidReason("voidReason");
		Errors errors = new BindException(encounter, "encounter");
		new EncounterValidator().validate(encounter, errors);
		Assert.assertFalse(errors.hasErrors());
	}
	
	/**
	 * @see EncounterValidator#validate(Object,Errors)
	 */
	@Test
	public void validate_shouldFailValidationIfFieldLengthsAreNotCorrect() {
		Encounter encounter = new Encounter();
		encounter.setEncounterType(new EncounterType());
		encounter.setPatient(new Patient());
		encounter.setEncounterDatetime(new Date());
		encounter
		        .setVoidReason("too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text");
		Errors errors = new BindException(encounter, "encounter");
		new EncounterValidator().validate(encounter, errors);
		Assert.assertTrue(errors.hasFieldErrors("voidReason"));
	}
}
