package com.epam.homework.model;

import java.util.List;

public record TableDefinition(int columnsAmount, List<CustomType> customTypes, int elementsAmount){
}
