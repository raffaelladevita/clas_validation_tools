package modules;

import org.jlab.clas.detector.DetectorResponse;
import org.jlab.clas.detector.ScintillatorResponse;
import validation.Event;
import validation.Module;
import org.jlab.groot.data.H1F;
import org.jlab.groot.group.DataGroup;

/**
 *
 * @author fizikci0147
 * @author devita
 */
public class CTOFModule extends Module {

    public CTOFModule() { super("CTOF"); }

    @Override
    public void createHistos() {
        H1F hsc_energy = new H1F("hsc_energy", "hsc_energy", 1000, 0.0, 300.0);
        hsc_energy.setTitleX("Energy");
        hsc_energy.setTitleY("Counts");
        DataGroup dscinth = new DataGroup(1, 1);
        dscinth.addDataSet(hsc_energy, 0);
        this.setHistos(dscinth);

    }

    @Override
    public void fillHistos(Event event) {
        for(int key : event.getCTOFMap().keySet()) {
            for(DetectorResponse r : event.getCTOFMap().get(key)) {
                ScintillatorResponse response = (ScintillatorResponse) r;
                this.getHistos().getH1F("hsc_energy").fill(response.getEnergy());
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
   /* @Override
    public void analyzeHistos() {this.fitGauss(this.getHistos().getH1F("hcher_nphe"),0,50);}*/
}
