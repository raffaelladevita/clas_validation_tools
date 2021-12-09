package modules;

import org.jlab.clas.detector.DetectorResponse;
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
public class CVTTRACKSModule extends Module {

    public CVTTRACKSModule() {
        super("CVTTRACK");
    }

    @Override
    public void createHistos() {
        H1F hchi2 = new H1F("hchi2", "hchi2", 100, 0, 10.0);
        hchi2.setTitleX("Chi2");
        hchi2.setTitleY("Counts");
        DataGroup ddctrk = new DataGroup(1, 1);
        ddctrk.addDataSet(hchi2, 0);
        this.setHistos(ddctrk);
    }
    @Override
    public void analyzeHistos() {
        // this.fitGauss(this.getHistos().getH1F("hcal_energy"),0.,2.);
    }

    @Override
    public void fillHistos(Event event) {
        for (int key : event.getCVTTrkMap().keySet()) {
            for (DetectorResponse r : event.getCVTTrkMap().get(key)) {
                // CalorimeterResponse response = (CalorimeterResponse) r;
                int pid = event.getParticles().get(0).pid();
                double Ep   = (int) event.getParticles().get(0).p();
                //    this.getHistos().getH1F("hcal_energy").fill(response.getEnergy());
            }
        }
    }

    @Override
    public void testHistos() {
        // double mean = this.getHistos().getH1F("hcal_energy").getMean();
        System.out.println("\n#############################################################");
        //System.out.println(String.format("mean = %.3f", mean));;
        System.out.println("#############################################################");
        //assertEquals(mean>0.2&&mean<0.3,true);

    }

}


