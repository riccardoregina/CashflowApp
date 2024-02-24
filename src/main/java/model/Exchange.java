package model;

public class Exchange {
    private Float usdTOeur = 0.93F;
    private Float eurTOusd = 1.08F;

    public Float getUsdTOeur() {
        return usdTOeur;
    }

    public void setUsdTOeur(Float usdTOeur) {
        this.usdTOeur = usdTOeur;
    }

    public Float getEurTOusd() {
        return eurTOusd;
    }

    public void setEurTOusd(Float eurTOusd) {
        this.eurTOusd = eurTOusd;
    }
}
