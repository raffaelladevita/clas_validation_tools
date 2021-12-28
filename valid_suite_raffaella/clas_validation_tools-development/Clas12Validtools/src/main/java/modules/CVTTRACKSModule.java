package modules;

import org.jlab.clas.detector.DetectorResponse;
import org.jlab.clas.physics.Particle;
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
        H1F hvzn = new H1F("hvzn", "hvzn", 100, 0, 10.0);
        hvzn.setTitleX("Vz");
        hvzn.setTitleY("Counts");
        H1F hvzp = new H1F("hvzp", "hvzp", 100, 0, 10.0);
        hvzp.setTitleX("Vz");
        hvzp.setTitleY("Counts");
        DataGroup dcvtrk = new DataGroup(2, 1);
        dcvtrk.addDataSet(hvzn, 0);
        dcvtrk.addDataSet(hvzp, 1);
        this.setHistos(dcvtrk);
    }
    @Override
    public void analyzeHistos() {
        // this.fitGauss(this.getHistos().getH1F("hcal_energy"),0.,2.);
    }

    @Override
    public void fillHistos(Event event) {
        for (int i =0;i<event.getParticles().size();i++) {
            Particle r = event.getParticles().get(i);
            int charge = r.charge();
            if (charge == 1) {
                this.getHistos().getH1F("hvzn").fill(r.vz());
            }else if(charge==-1){
                this.getHistos().getH1F("hvzp").fill(r.vz());
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


