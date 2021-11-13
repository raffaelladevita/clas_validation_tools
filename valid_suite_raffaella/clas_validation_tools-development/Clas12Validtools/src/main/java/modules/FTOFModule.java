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
public class FTOFModule extends Module {

    public FTOFModule() {
        super("FTOF");
    }
    
    @Override
    public void createHistos() {
        H1F hsc_energy = new H1F("hsc_energy", "hsc_energy", 100, 0.0, 50.0);
        hsc_energy.setTitleX("Energy");
        hsc_energy.setTitleY("Counts");
        DataGroup dscinth = new DataGroup(1, 1);
        dscinth.addDataSet(hsc_energy, 0);
        this.setHistos(dscinth);
    }
    
    @Override
    public void fillHistos(Event event) {
        for(int key : event.getFTOFMap().keySet()) {
            for(DetectorResponse r : event.getFTOFMap().get(key)) {
                ScintillatorResponse response = (ScintillatorResponse) r;
                this.getHistos().getH1F("hsc_energy").fill(response.getEnergy());
            }
        }
    }

}
