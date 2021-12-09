package modules;

import org.jlab.clas.detector.DetectorResponse;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import validation.Event;
import validation.Module;
import org.jlab.groot.data.H1F;
import org.jlab.groot.group.DataGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 *
 * @author fizikci0147
 * @author devita
 */
public class EventBuilderModule extends Module {

    public EventBuilderModule() { super("Particle"); }

    @Override
    public void createHistos() {
        H1F h_m1 = new H1F("h_m1", "h_m1", 500, 0.0, 10);
        h_m1.setTitleX("Mpi0");
        h_m1.setTitleY("Counts");

        H1F h_m2 = new H1F("h_m1", "h_m1", 500, 0.0, 10);
        h_m2.setTitleX("Mpi0");
        h_m2.setTitleY("Counts");

       DataGroup devent = new DataGroup(2, 1);
       devent.addDataSet(h_m1, 0);
        devent.addDataSet(h_m2, 1);
        this.setHistos(devent);

    }

    @Override
    public void fillHistos(Event event) {

        ArrayList<Particle> Ecalphoton = new ArrayList<>();
        ArrayList<Particle> FTcalphoton = new ArrayList<>();

        double mass1=0., mass2=0.;
        for (int i =0;i<event.getParticles().size();i++) {
            Particle r = event.getParticles().get(i);
            if (r.pid() == 22&&event.getECALMap().get(i)!=null) {
                Ecalphoton.add(r);
                LorentzVector v0 = r.vector();
                LorentzVector v1 = r.vector();
                LorentzVector vt = new LorentzVector(v0);
                vt.add(v1);
                mass1 = vt.mass();
            }
            if(Ecalphoton.size()>=2){
            this.getHistos().getH1F("h_m1").fill(mass1);}

            if (r.pid() == 22&&event.getFTMap().get(i)!=null) {
                FTcalphoton.add(r);
                LorentzVector v0 = r.vector();
                LorentzVector v1 = r.vector();
                LorentzVector vt = new LorentzVector(v0);
                vt.add(v1);
                mass2 = vt.mass();
            }

            if(FTcalphoton.size()>=2) {
                this.getHistos().getH1F("h_m2").fill(mass2);
            }
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