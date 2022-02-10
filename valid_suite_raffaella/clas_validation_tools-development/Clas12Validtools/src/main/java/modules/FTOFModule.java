package modules;

import org.jlab.clas.detector.CherenkovResponse;
import org.jlab.clas.detector.DetectorResponse;
import org.jlab.clas.detector.ScintillatorResponse;
import org.jlab.clas.physics.Particle;
import validation.Event;
import validation.Module;
import org.jlab.groot.data.H1F;
import org.jlab.groot.group.DataGroup;

/**
 *
 * @author fizikci0147
 * @author devita
 */
public class FTOFModule extends Module {

    public FTOFModule() {
        super("FTOF");
    }
    
    @Override
    public void createHistos() {
        H1F hsc_energy = new H1F("hsc_energy", "hsc_energy", 1000, 0.0, 300.0);
        hsc_energy.setTitleX("Energy");
        hsc_energy.setTitleY("Counts");
        H1F hsvt = new H1F("hsvt", "hsvt", 1000, -170.0, -130.0);
        hsvt.setTitleX("Electron Vertex Time");
        hsvt.setTitleY("Counts");
        DataGroup dscinth = new DataGroup(1, 1);
        dscinth.addDataSet(hsvt, 0);
        dscinth.addDataSet(hsc_energy, 1);
        dscinth.addDataSet(hsc_energy, 2);
        dscinth.addDataSet(hsc_energy, 3);
        this.setHistos(dscinth);
    }
    
    @Override
    public void fillHistos(Event event) {

        if (event.getParticles().size() > 0 &&event.getFTOFMap().get(0)!=null) {
            int pid = event.getParticles().get(0).pid();
            int status = (int) event.getParticles().get(0).getProperty("status");
            int detector = (int) Math.abs(status) / 1000;
            double c = 3.e1;//cm/ns
            double vt =event.getParticles().get(0).getProperty("vt");
            if (pid == 11) {
                for(int key : event.getFTOFMap().keySet()) {
                for (DetectorResponse r : event.getFTOFMap().get(key)) {
                    ScintillatorResponse response = (ScintillatorResponse) r;
                    int layer = response.getDescriptor().getLayer();
                    if (layer == 2) {
                        double vertt = response.getTime() - response.getPath()/c - vt;
                        this.getHistos().getH1F("hsvt").fill(vertt);

                    }
                }
                }
            }
        }
    }


    @Override
    public void testHistos() {
        //double npe = this.getHistos().getH1F("hcher_nphe").getMean();
        System.out.println("\n#############################################################");
        //System.out.println(String.format("npe/Events = %.3f", npe));;
        System.out.println("#############################################################");
        //  assertEquals(npe>0.15,true);

    }
    @Override
    public void analyzeHistos() {this.fitGauss(this.getHistos().getH1F("hsvt"),-160,-140);}

}
