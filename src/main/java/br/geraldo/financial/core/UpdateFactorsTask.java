package br.geraldo.financial.core;

import java.util.TimerTask;

public class UpdateFactorsTask extends TimerTask {

	@Override
	public void run() {
		CalculationModule.updateFactorMap();
	}

}
