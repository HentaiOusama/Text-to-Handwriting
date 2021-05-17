package com.hentai_productions;

public class SpecialSymbol {

    public final boolean isSpecialSymbol;
    private final int symbolCode, symbolLength;

    SpecialSymbol(boolean isSpecialSymbol) {
        this.isSpecialSymbol = isSpecialSymbol;
        this.symbolCode = 1;
        this.symbolLength = 0;
    }

    SpecialSymbol(boolean isSpecialSymbol, int symbolCode, int symbolLength) {
        this.isSpecialSymbol = isSpecialSymbol;
        this.symbolCode = symbolCode;
        this.symbolLength = symbolLength;
    }

    public int getSymbolCode() {
        return symbolCode;
    }

    public int getSymbolLength() {
        return symbolLength;
    }
}