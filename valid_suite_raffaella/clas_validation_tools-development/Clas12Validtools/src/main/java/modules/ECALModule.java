package modules;

import org.jlab.clas.detector.DetectorResponse;
import org.jlab.clas.detector.CalorimeterResponse;
import validation.Event;
import validation.Module;
import org.jlab.groot.data.H1F;
import org.jlab.groot.group.DataGroup;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author fizikci0147
 * @author devita
 */
public class ECALModule extends Module {

    public ECALModule() {
        super("ECAL");
    }

    @Override
    public void createHistos() {
        H1F hcal_energy = new H1F("hcal_energy", "hcal_energy", 100, 0., 2.0);
        hcal_energy.setTitleX("Energy");
        hcal_energy.setTitleY("Counts");
        DataGroup dscalh = new DataGroup(1, 1);
        dscalh.addDataSet(hcal_energy, 0);
        this.setHistos(dscalh);
    }
    @Override
    public void analyzeHistos() {
        this.fitGauss(this.getHistos().getH1F("hcal_energy"),0.,0.5);
    }

    @Override
    public void fillHistos(Event event) {
        if (event.getParticles().size() > 0) {

        for (int key : event.getECALMap().keySet()) {
            for (DetectorResponse r : event.getECALMap().get(key)) {
                CalorimeterResponse response = (CalorimeterResponse) r;
                int pid = event.getParticles().get(0).pid();
                double Ep   =  event.getParticles().get(0).p();
                if(pid==11) {
                    this.getHistos().getH1F("hcal_energy").fill(response.getEnergy()/Ep);
                }
            }
        }
    }
    }

    @Override
    public void testHistos() {
        double mean = this.getHistos().getH1F("hcal_energy").getMean();
        System.out.println("\n#############################################################");
        System.out.println(String.format("mean = %.3f", mean));;
        System.out.println("#############################################################");
        //assertEquals(mean>0.2&&mean<0.3,true);

    }

}
