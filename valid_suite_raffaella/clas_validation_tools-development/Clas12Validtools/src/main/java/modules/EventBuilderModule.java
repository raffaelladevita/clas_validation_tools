package modules;

import org.jlab.clas.detector.DetectorResponse;
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
public class EventBuilderModule extends Module {

    public EventBuilderModule() { super("Particle"); }

    @Override
    public void createHistos() {
        H1F h_px = new H1F("h_px", "h_px", 100, -0.1, 0.1);
        h_px.setTitleX("Px");
        h_px.setTitleY("Counts");
       DataGroup devent = new DataGroup(1, 1);
       devent.addDataSet(h_px, 0);
       this.setHistos(devent);

    }

    @Override
    public void fillHistos(Event event) {

        for (Particle r : event.getParticles()) {
                this.getHistos().getH1F("h_px").fill(r.px());
        }
    }
    @Override
    public void testHistos() {
  /*      double mean = this.getHistos().getH1F("hcal_energy").getMean();
        System.out.println("\n#############################################################");
        System.out.println(String.format("mean = %.3f", mean));;
        System.out.println("#############################################################");
         //assertEquals(mean>0.2&&mean<0.3,true);
*/
    }
}