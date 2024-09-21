public enum FunctionType {
    LINEAR("линейная"),
    POLY2("полином 2-й степени"),
    POLY3("полином 3-й степени"),
    EXP("экспоненциальная"),
    LOG("логарифмическая"),
    POWER("степенная"),
    BEST("наилучшая");

    private String name;

    FunctionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static FunctionType getFunctionTypeByName(String name) {
        for (FunctionType functionType : FunctionType.values()) {
            if (functionType.getName().equalsIgnoreCase(name)) {
                return functionType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
