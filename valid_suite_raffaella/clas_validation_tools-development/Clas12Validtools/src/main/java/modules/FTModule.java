package modules;

import org.jlab.clas.detector.DetectorResponse;
import org.jlab.clas.detector.TaggerResponse;
import validation.Event;
import validation.Module;
import org.jlab.groot.data.H1F;
import org.jlab.groot.group.DataGroup;

/**
 *
 * @author fizikci0147
 * @author devita
 */
public class FTModule extends Module {

    public FTModule() { super("FT"); }

    @Override
    public void createHistos() {
        H1F hft_energy = new H1F("hft_energy", "hft_energy", 100, 0.0, 10.0);
        hft_energy.setTitleX("Energy");
        hft_energy.setTitleY("Counts");
        DataGroup dft = new DataGroup(1, 1);
        dft.addDataSet(hft_energy, 0);
        this.setHistos(dft);

    }

    @Override
    public void fillHistos(Event event) {
        for(int key : event.getFTMap().keySet()) {
            for(DetectorResponse r : event.getFTMap().get(key)) {
                TaggerResponse response = (TaggerResponse) r;
                this.getHistos().getH1F("hft_energy").fill(response.getEnergy());
            }
        }
    }

}
