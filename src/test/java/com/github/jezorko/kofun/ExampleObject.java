package com.github.jezorko.kofun;

public class ExampleObject {

    private final int primitiveField;
    private final ExampleInnerObject innerObjectField;

    public ExampleObject(int primitiveField, ExampleInnerObject innerObjectField) {
        this.primitiveField = primitiveField;
        this.innerObjectField = innerObjectField;
    }

    public int getPrimitiveField() {
        return primitiveField;
    }

    public ExampleInnerObject getInnerObjectField() {
        return innerObjectField;
    }

    public static class ExampleInnerObject {
        private final int primitiveField;

        public ExampleInnerObject(int primitiveField) {
            this.primitiveField = primitiveField;
        }

        public int getPrimitiveField() {
            return primitiveField;
        }
    }
}
