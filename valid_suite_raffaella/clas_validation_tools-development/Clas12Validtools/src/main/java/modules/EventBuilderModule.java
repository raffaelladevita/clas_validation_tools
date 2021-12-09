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
        H1F h_p = new H1F("h_p", "h_p", 500, 0.0, 10);
        h_p.setTitleX("P");
        h_p.setTitleY("Counts");
       DataGroup devent = new DataGroup(1, 1);
       devent.addDataSet(h_p, 0);
       this.setHistos(devent);

    }

    @Override
    public void fillHistos(Event event) {

        for (Particle r : event.getParticles()) {
                this.getHistos().getH1F("h_p").fill(r.p());
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