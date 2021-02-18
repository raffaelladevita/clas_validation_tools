package Clas12Validtools.src.main.java;

import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.pdg.PDGParticle;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.Vector3;

import java.util.HashMap;

public class Validation_Particle extends Particle {
    private int particleCharge;
    public double particle_vt;
    public double beta;
    private LorentzVector partVector;
    private Vector3 partVertex;
    private HashMap particleProperties;
    private int particleID;
    private int particleGeantID;

    public Validation_Particle(int pidCode, float px, float py, float pz, float vx, float vy, float vz, float vt,float beta) {
        this.initParticle(pidCode, px, py, pz, vx, vy, vz, vt);
        this.beta = beta;
    }

    /*public void New_Particle(int pid, double px, double py, double pz, double vx, double vy, double vz, double vt) {
        this.initParticle(pid, px, py, pz, vx, vy, vz, vt);
    }*/


    public final void initParticleWithMass(double mass, double px, double py, double pz, double vx, double vy, double vz, double vt) {
        //System.out.println("Is it being called");
        this.particleCharge = 0;
        this.partVector = new LorentzVector();
        this.partVertex = new Vector3(vx, vy, vz);
        this.partVector.setPxPyPzM(px, py, pz, mass);
        this.particleProperties = new HashMap();
        this.particle_vt = vt;
        //System.out.println(particle_vt);
    }

    public final void initParticle(int pid, double px, double py, double pz, double vx, double vy, double vz, double vt) {
        PDGParticle particle = PDGDatabase.getParticleById(pid);
        if (particle == null) {
            System.out.println("Particle: warning. particle with pid=" + pid + " does not exist.");
            this.initParticleWithMass(0.0D, px, py, pz, vx, vy, vz,vt);
            this.particleID = 0;
            this.particleGeantID = 0;
        } else {
            this.initParticleWithMass(particle.mass(), px, py, pz, vx, vy, vz,vt);
            this.particleID = pid;
            this.particleGeantID = 0;
            this.particleCharge = (byte) particle.charge();
        }

    }
}
