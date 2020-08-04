package org.sunbird.schema.impl;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.sunbird.schema.ISchemaValidator;
import org.sunbird.schema.SchemaValidatorFactory;
import org.sunbird.schema.dto.ValidationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestBaseSchemaValidator {

    static ISchemaValidator validator;

    @BeforeClass
    public static void init(){
        try{
            validator = SchemaValidatorFactory.getInstance("content","1.0");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testGetJsonProps() {
        try{
            List<String> jsonProps = validator.getJsonProps();
            Assert.assertNotNull(jsonProps);
            Assert.assertFalse(jsonProps.isEmpty());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testGetCategoryProps() {
        try{
            validator = SchemaValidatorFactory.getInstance("category","1.0");
            List<String> jsonProps = validator.getJsonProps();
            System.out.println("jsonProps :::: "+jsonProps);
            Map<String, Object> input = new HashMap<String, Object>(){{
                put("name", "Test Category");
                put("identifier", "cat-test-01");
                put("status", "Live");
                put("objectTypes", "Test");
            }};
            ValidationResult result = validator.getStructuredData(input);
            System.out.println("result metadata ::: "+result.getMetadata());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
