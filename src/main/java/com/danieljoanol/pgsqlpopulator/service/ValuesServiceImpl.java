package com.danieljoanol.pgsqlpopulator.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.danieljoanol.pgsqlpopulator.model.Chars;
import com.danieljoanol.pgsqlpopulator.model.GenericType;

import net.datafaker.Faker;

@Service
public class ValuesServiceImpl implements ValuesService {

    Faker faker = new Faker(new Locale("en-US"));
    Random random = new Random();

    @Override
    public List<String> generateValues(GenericType field, Integer recordsNumber) {
        
        switch (field.getType()) {

            case CHAR, ENUM, SMALL_INT, INTEGER, BIG_INT, MONEY, BOOLEAN:
                field.setLength(null);
                return generateValue(field.getType().toString(), field, recordsNumber);

            case VARCHAR, TEXT:
                return generateValue(field.getVarcharType().toString(), field, recordsNumber);
        
            default:
                return null;
        }
        
    }

    private List<String> generateValue(String type, GenericType field, Integer recordsNumber) {
        
        List<String> strValues = new ArrayList<>();
        String strValue;
        List<Integer> intValues = new ArrayList<>();
        List<Long> longValues = new ArrayList<>();

        switch (type) {

            case "CHAR":
                
                if (recordsNumber < strValues.size()) {
                    strValues = Arrays.asList(Chars.array);
                } else {
                    List<String> valuesA = Arrays.asList(Chars.array);
                    List<String> valuesB = Arrays.asList(Chars.array);
                    strValues = Stream.concat(valuesA.stream(), valuesB.stream())
                            .collect(Collectors.toList());
                }

                break;

            case "FIRST_NAME":
                
                strValues = faker.collection(
                        () -> faker.name().firstName())
                .len(recordsNumber)
                .generate();
                
                break;

            case "LAST_NAME":
                
                strValues = faker.collection(
                    () -> faker.name().lastName())
                .len(recordsNumber)
                .generate();
                
                break;

            case "FULL_NAME":
                
                strValues = faker.collection(
                    () -> faker.name().fullName())
                .len(recordsNumber)
                .generate();
                
                break;

            case "ADDRESS":
                
                strValues = faker.collection(
                    () -> faker.address().fullAddress())
                .len(recordsNumber)
                .generate();
                
                break;

            case "COMPANY":
                
                strValues = faker.collection(
                    () -> faker.company().name())
                .len(recordsNumber)
                .generate();
                
                break;

            case "ID_NUMBER":
                
                strValues = faker.collection(
                    () -> faker.idNumber().valid())
                .len(recordsNumber)
                .generate();
                
                break;

            case "PHONE_NUMBER":
                strValues = faker.collection(
                    () -> faker.phoneNumber().cellPhone())
                .len(recordsNumber)
                .generate();
                break;

            case "ENUM":

                if (field.getItems() == null) {
                    throw new IllegalArgumentException("Items can't be null");
                }

                String[] items = field.getItems();
                Integer n = random.nextInt(items.length);

                for (int i = 0; i < recordsNumber; i++) {
                    strValue = items[n];
                    strValues.add(strValue);
                }
                
                break;

            case "SMALL_INT":

                intValues = faker.collection(
                    () -> faker.number().numberBetween(-32768, 32767))
                .len(recordsNumber)
                .generate();

                strValues = intValues.stream().map(String::valueOf).collect(Collectors.toList());

                break;

            case "INTEGER":

                intValues = faker.collection(
                    () -> faker.number().numberBetween(-2147483648, 2147483647))
                .len(recordsNumber)
                .generate();

                strValues = intValues.stream().map(String::valueOf).collect(Collectors.toList());

                break;

            case "BIG_INT", "MONEY":

                longValues = faker.collection(
                    () -> faker.number().numberBetween(-9223372036854775808L, 9223372036854775807L))
                .len(recordsNumber)
                .generate();

                strValues = longValues.stream().map(String::valueOf).collect(Collectors.toList());

                break;

            case "BOOLEAN":

                intValues = faker.collection(
                    () -> faker.number().numberBetween(0, 2))
                .len(recordsNumber)
                .generate();

                strValues = intValues.stream().map(this::createBoolean).collect(Collectors.toList());


        }

        if (field.getLength() != null ) {
            for (int i = 0; i < recordsNumber; i++) {
                strValue = strValues.get(i);
    
                if (strValue.length() > field.getLength()) {
                    strValue = strValue.substring(0, field.getLength());
                }
                
            }
        }

        return strValues;
    }

    private String createBoolean(Integer n) {
        
        if (n == 1) {
            return "false";
        } else {
            return "true";
        }
    }

}
