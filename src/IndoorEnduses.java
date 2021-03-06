import sim.engine.SimState;
import sim.util.distribution.*;

/** @author amashha */
public class IndoorEnduses {
  private double toilet;
  private double clothes;
  private double faucet;
  private double leak;
  private double shower;
  private double bath;
  private double dish;
  private double other;

  public IndoorEnduses(
      SimState state, double randomNum, int sizeOfHousehold, int builtYear, String scenario) {
    String theScenario = scenario;
    if (theScenario.equals("probabilistic")) calculateAll(state, randomNum, sizeOfHousehold);
    if (theScenario.equals("enduse")) calculateAll(state, randomNum, sizeOfHousehold, builtYear);
  }

  private void calculateToilet(SimState state, double randomNum, int sizeOfHousehold) {
    SimState wrrSim = state;
    double[] volumeDist = {
      0.3, 0.5, 0.8, 1, 1.3, 1.5, 1.8, 2, 2.3, 2.5, 2.8, 3, 3.3, 3.5, 3.8, 4, 4.3, 4.5, 4.8, 5, 10
    };
    double[] distribution = {
      0, 0, 0, 0, 1, 4, 16, 30, 43, 52, 59, 64, 70, 76, 81, 85, 90, 92, 94, 96, 100
    };
    double volume = 0;
    for (int j = 0; j < distribution.length; j++) {
      if (randomNum < distribution[j]) {
        volume = volumeDist[j]; // kGal per Year to Gal per month
        break;
      }
    }
    Gamma dist = new Gamma(1.149758454, 5.472850242, wrrSim.random);
    int flushes = (int) Math.round(dist.nextDouble());
    toilet = sizeOfHousehold * volume * flushes;
  }

  private void calculateShower(SimState state, double randomNum) {
    SimState wrrSim = state;
    double[] volumeDist = {
      0, 2.5, 5, 7.5, 10, 12.5, 15, 17.5, 20, 22.5, 25, 27.5, 30, 32.5, 35, 37.5, 40, 42.5, 45,
      47.5, 50, 100
    };
    double[] distribution = {
      0, 0, 0, 2, 9, 20, 36, 51, 65, 78, 86, 92, 94, 95, 96, 97, 97, 98, 99, 99, 100, 100
    };
    double volume = 0;
    for (int j = 0; j < distribution.length; j++) {
      if (randomNum < distribution[j]) {
        volume = volumeDist[j]; // kGal per Year to Gal per month
        break;
      }
    }
    Exponential dist = new Exponential(1.97, wrrSim.random);
    int showers = (int) Math.round(dist.nextDouble());
    shower = volume * showers;
  }

  private void calculateClothes(SimState state, double randomNum) {
    SimState wrrSim = state;
    double[] volumeDist = {
      5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 120
    };
    double[] distribution = {
      0, 0, 3, 11, 20, 29, 43, 61, 79, 90, 94, 96, 98, 100, 100, 100, 100, 100, 100, 100, 100
    };
    double volume = 0;
    for (int j = 0; j < distribution.length; j++) {
      if (randomNum < distribution[j]) {
        volume = volumeDist[j]; // kGal per Year to Gal per month
        break;
      }
    }
    Exponential dist = new Exponential(0.96, wrrSim.random);
    int washes = (int) Math.round(dist.nextDouble());
    clothes = volume * washes;
  }

  private void calculateFaucet(double randomNum) {
    double[] volumeDist = new double[22];
    for (int i = 0; i < volumeDist.length; i++) {
      volumeDist[i] = (i + 1) * 5;
    }

    double[] distribution = {
      4, 11, 21, 35, 47, 57, 68, 75, 79, 84, 86, 88, 90, 92, 93, 94, 95, 96, 97, 97, 97, 100
    };
    double volume = 0;
    for (int j = 0; j < distribution.length; j++) {
      if (randomNum < distribution[j]) {
        volume = volumeDist[j]; // kGal per Year to Gal per month
        break;
      }
    }
    faucet = volume;
  }

  private void calculateDish(SimState state, double randomNum) {
    SimState wrrSim = state;
    Gamma dist = new Gamma(1.142857143, 1.142857143, wrrSim.random);
    dish = dist.nextDouble();
  }

  private void calculateLeak(double randomNum) {
    double[] volumeDist = new double[22];
    for (int i = 0; i < volumeDist.length; i++) {
      volumeDist[i] = (i) * 10;
    }

    double[] distribution = {
      47, 66, 75, 81, 85, 87, 90, 92, 93, 95, 96, 97, 97, 97, 97, 98, 98, 98, 99, 99, 100
    };
    double volume = 0;
    for (int j = 0; j < distribution.length; j++) {
      if (randomNum * 100 < distribution[j]) {
        volume =
            volumeDist[j] + randomNum * (volumeDist[j + 1] - volumeDist[j]); // kGal/yr to Gal/Mo
        break;
      }
    }
    leak = 0;
  }

  private void calculateBath(SimState state, double randomNum) {
    SimState wrrSim = state;
    Normal dist = new Normal(3.2, 1, wrrSim.random);
    bath = 0;
  }

  private void calculateOther(SimState state, double randomNum) {
    SimState wrrSim = state;
    Normal dist = new Normal(7.8, 2, wrrSim.random);
    other = 0;
  }

  public void calculateAll(SimState state, double randomNum, int sizeOfHousehold) {

    calculateToilet(state, randomNum, sizeOfHousehold);
    calculateShower(state, randomNum);
    calculateClothes(state, randomNum);
    calculateLeak(randomNum);
    calculateFaucet(randomNum);
    calculateDish(state, randomNum);
    calculateBath(state, randomNum);
    calculateOther(state, randomNum);
  }

  public void calculateAll(SimState state, double randomNum, int sizeOfHousehold, int builtYear) {
    // double showerHead , toiletFlush, clothVolume, faucetVolume;
    int applianceYear = 0;

    if (builtYear <= 2030 && state.random.nextDouble(true, true) < 0.20) {
      applianceYear = 1990;
    } else if (builtYear <= 2030) {
      applianceYear = 2010;
    }

    switch (applianceYear) {
        /*case 1950:
        	this.shower = (5 + state.random.nextDouble(true, true) * (8 - 5)) * 5.3 * sizeOfHousehold;
        	this.toilet = 7 * 5.1 * sizeOfHousehold;
        	this.faucet = (3 + state.random.nextDouble(true, true) * (7 - 3)) * 8.1;
        	this.dish = 14 * 0.1;
        	this.clothes = 56 * 0.37;

        	calculateLeak(randomNum); // Based on probability distribution
        	calculateBath(state, randomNum);// Based on probability distribution
        	calculateOther(state, randomNum);// Based on probability distribution

        	break;

        case 1980:
        	this.shower = (5 + state.random.nextDouble(true, true) * (8 - 5)) * 5.3;
        	this.toilet = 5.25 * 5.1 * sizeOfHousehold;
        	this.faucet = (3 + state.random.nextDouble(true, true) * (7 - 3)) * 8.1;
        	this.dish = 14 * 0.1;
        	this.clothes = 56 * 0.37;

        	calculateLeak(randomNum); // Based on probability distribution
        	calculateBath(state, randomNum);// Based on probability distribution
        	calculateOther(state, randomNum);// Based on probability distribution

        	break;*/
      case 1990:
        this.shower = (2.75 + state.random.nextDouble(true, true) * (4 - 2.75)) * 5.3;
        this.toilet =
            (3.5 + state.random.nextDouble(true, true) * (4.5 - 3.5)) * 5.1 * sizeOfHousehold;
        this.faucet = (2.75 + state.random.nextDouble(true, true) * (3 - 2.75)) * 8.1;
        this.dish = (9.5 + state.random.nextDouble(true, true) * (14 - 9.5)) * 0.1;
        this.clothes = (43 + state.random.nextDouble(true, true) * (51 - 43)) * 0.37;

        calculateLeak(randomNum); // Based on probability distribution
        calculateBath(state, randomNum); // Based on probability distribution
        calculateOther(state, randomNum); // Based on probability distribution

        break;
      case 2010:
        this.shower = 2.5 * 5.3;
        this.toilet = (1 + state.random.nextDouble(true, true) * (1.6 - 1)) * 5.1 * sizeOfHousehold;
        this.faucet = (1 + state.random.nextDouble(true, true) * (1.7 - 1)) * 8.1;
        this.dish = (7 + state.random.nextDouble(true, true) * (10.5 - 7)) * 0.1;
        this.clothes = (27 + state.random.nextDouble(true, true) * (39 - 27)) * 0.37;

        calculateLeak(randomNum); // Based on probability distribution
        calculateBath(state, randomNum); // Based on probability distribution
        calculateOther(state, randomNum); // Based on probability distribution

        break;
      default:
        this.shower = 1.4 * 5.3;
        this.toilet = 1 * 5.1 * sizeOfHousehold;
        this.faucet = 1.5 * 8.1;
        this.dish = 4.5 * 0.1;
        this.clothes = 27 * 0.37;

        calculateLeak(randomNum); // Based on probability distribution
        calculateBath(state, randomNum); // Based on probability distribution
        calculateOther(state, randomNum); // Based on probability distribution

        break;
    }
  }

  public double getIndoorDemand() {
    return toilet + shower + clothes + faucet + dish + leak + bath + other;
  }
}
