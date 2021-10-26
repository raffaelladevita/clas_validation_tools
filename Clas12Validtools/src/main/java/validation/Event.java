package validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.clas.detector.CherenkovResponse;
import org.jlab.clas.detector.DetectorResponse;
import org.jlab.clas.detector.ScintillatorResponse;
import org.jlab.clas.physics.Particle;
import org.jlab.detector.base.DetectorType;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;

/**
 *
 * @author fizikci0147
 */
public class Event {
    private boolean debug = false;
    
    private DataBank runBank = null;
    private DataBank mcBank = null;
    private DataBank recBank = null; 
    private DataBank recPartBank = null; 
    private DataBank recFtPartBank = null; 
    private DataBank recTrkBank = null; 
    private DataBank recFtBank = null;
    private DataBank recCalBank = null;
    private DataBank recSciBank = null;
    private DataBank recCheBank = null;

    private List<Particle> particles = new ArrayList<>();
    private List<Particle> mcParticles = new ArrayList<>();
    private Map<Integer, List<DetectorResponse>> ecalMap = new HashMap<>();
    private Map<Integer, List<DetectorResponse>> htccMap = new HashMap<>();
    private Map<Integer, List<DetectorResponse>> ftofMap = new HashMap<>();
    private Map<Integer, List<DetectorResponse>> ctofMap = new HashMap<>();
    private Map<Integer, List<DetectorResponse>>  cndMap = new HashMap<>();
    private Map<Integer, List<DetectorResponse>>   dcMap = new HashMap<>();
    private Map<Integer, List<DetectorResponse>>  cvtMap = new HashMap<>();
    private Map<Integer, List<DetectorResponse>>   ftMap = new HashMap<>();


    public Event(DataEvent event) {
        this.readEvent(event);
        if(debug) System.out.println("Read event with " + particles.size() + " particles");
    }
    


    private DataBank getBank(DataEvent de, String bankName) {
        DataBank bank = null;
        if (de.hasBank(bankName)) {
            bank = de.getBank(bankName);
        }
        return bank;
    }

    private void getBanks(DataEvent de) {
        runBank       = getBank(de, "RUN::config");
        mcBank        = getBank(de, "MC::Particle");
        recBank       = getBank(de, "REC::Event");
        recPartBank   = getBank(de, "REC::Particle");
        recFtPartBank = getBank(de, "RECFT::Particle");
        recCheBank    = getBank(de, "REC::Cherenkov");
        recCalBank    = getBank(de, "REC::Calorimeter");
        recSciBank    = getBank(de, "REC::Scintillator");
        recTrkBank    = getBank(de, "REC::Track");
        recFtBank     = getBank(de, "REC::ForwardTagger");
    }

    private void readParticles() {
        if(recPartBank!=null) {
            int rows = recPartBank.rows();
            //System.out.println("rows recbank: ");
            // System.out.println(rows);
            for (int loop = 0; loop < rows; loop++) {
                int pid    = recPartBank.getInt("pid", loop);
                int charge = recPartBank.getByte("charge", loop);
                if(pid==0) {
                    if (recPartBank.getByte("charge", loop) == -1)     pid = -211;
                    else if (recPartBank.getByte("charge", loop) == 1) pid = 211;
                    else pid = 22;
                }
                Particle recParticle = new Particle(
                        pid,
                        recPartBank.getFloat("px", loop),
                        recPartBank.getFloat("py", loop),
                        recPartBank.getFloat("pz", loop),
                        recPartBank.getFloat("vx", loop),
                        recPartBank.getFloat("vy", loop),
                        recPartBank.getFloat("vz", loop));
                double vt  = recPartBank.getFloat("vt", loop);
                int status = recPartBank.getInt("status", loop);
                recParticle.setProperty("vt", vt);
                recParticle.setProperty("status", status);
                particles.add(recParticle);
            }
        }
    }
            
    private void readScintillatorResponses() {
        if(recSciBank!=null) {
            ftofMap.clear();
            ctofMap.clear();
            cndMap.clear();
            int rows = recSciBank.rows();
            for (int loop = 0; loop < rows; loop++) {
                int index     = recSciBank.getInt("index", loop);
                int pindex    = recSciBank.getInt("pindex", loop);
                int detector  = recSciBank.getByte("detector", loop);
                int layer     = recSciBank.getByte("layer", loop);
                int sector    = recSciBank.getByte("sector", loop);
                int paddle    = recSciBank.getInt("component", loop);
                ScintillatorResponse response = new ScintillatorResponse(layer, sector, paddle);
                double energy = recSciBank.getFloat("energy", loop);
                double time   = recSciBank.getFloat("time", loop);
                double x      = recSciBank.getFloat("x", loop);
                double y      = recSciBank.getFloat("y", loop);
                double z      = recSciBank.getFloat("z", loop);
                double hx     = recSciBank.getFloat("hx", loop);
                double hy     = recSciBank.getFloat("hy", loop);
                double hz     = recSciBank.getFloat("hz", loop);
                double path   = recSciBank.getFloat("path", loop);
                response.setHitIndex(index);
                response.setPosition(x,y,z);
                response.setEnergy(energy);
                response.setEnergy(time);
                response.setMatchPosition(hx, hy, hz);
                response.setPath(path);
                response.setAssociation(pindex);
                // FIXME add dE/dx and cluster size
                if(detector==DetectorType.FTOF.getDetectorId()) {
                    this.loadMap(ftofMap, response);
                }
                else if(detector==DetectorType.CTOF.getDetectorId()) {
                    this.loadMap(ctofMap, response);
                }
                else if(detector==DetectorType.CND.getDetectorId())  {
                    this.loadMap(cndMap, response);
                }
            }           
        }    
    }        
    // ADD other detectors
//    //Calorimeter
//        if (event.hasBank("REC::Calorimeter")) {
//            C12validtools Response = new C12validtools();
//            // DetectorType type = null;
//            int rows = recCalBank.rows();
//            //  System.out.println("rows Calo: ");
//            //  System.out.println(rows);
//            for (int loop = 0; loop < rows; loop++) {
//                int sector = recCalBank.getByte("sector", loop);
//                int layer = recCalBank.getByte("layer", loop);
//                float x = recCalBank.getFloat("x", loop);
//                float y = recCalBank.getFloat("y", loop);
//                float z = recCalBank.getFloat("z", loop);
//                float hx = recCalBank.getFloat("hx", loop);
//                float hy = recCalBank.getFloat("hy", loop);
//                float hz = recCalBank.getFloat("hz", loop);
//                float path = recCalBank.getFloat("path", loop);
//                float energy = recCalBank.getFloat("energy", loop);
//                //float u = recCalBank.getFloat("widthu",loop);
//                //float v = recCalBank.getFloat("widthu",loop);
//                //float w = recCalBank.getFloat("widthw",loop);
//                //Response.getDescriptor().setType(type);
//                Response.setPosition(x, y, z);
//                Response.setHitIndex(loop);
//                Response.setEnergy(energy);
//                Response.setTime(recCalBank.getFloat("time", loop));
//                Response.setStatus(recCalBank.getInt("status", loop));
//                dataGroups.getItem(2).getH1F("hcal_energy").fill(energy);
//                dataGroups.getItem(2).getH1F("hcal_path").fill(path);
//            }
//            Calo_List.add(Response);
//        }
    private void readCherenkovResponses() {
        if(recCheBank!=null) {
            htccMap.clear();
            int rows = recCheBank.rows();
            //  System.out.println("rows Cherenkov: ");
            //  System.out.println(rows);
            for (int loop = 0; loop < rows; loop++) {
                int index     = recCheBank.getInt("index", loop);
                int pindex    = recCheBank.getInt("pindex", loop);
                int detector  = recCheBank.getByte("detector", loop);
                float x       = recCheBank.getFloat("x", loop);
                float y       = recCheBank.getFloat("y", loop);
                float z       = recCheBank.getFloat("z", loop);
                double time   = recCheBank.getFloat("time", loop);
                double nphe   = recCheBank.getFloat("nphe", loop);
                double dtheta = recCheBank.getFloat("dtheta", loop);
                double dphi   = recCheBank.getFloat("dphi", loop);
                CherenkovResponse response = new CherenkovResponse(dtheta,dphi);
                response.setHitIndex(index);
                response.setAssociation(pindex);
                response.setEnergy(nphe);
                response.setTime(time);
                response.setPosition(x, y, z);
                if(detector == DetectorType.HTCC.getDetectorId()) {
                    this.loadMap(htccMap, response);
                }
            }
        }
    }
//// FORWARD TAGGER
//        if (event.hasBank("REC::ForwardTagger")) {
//            C12validtools Response = new C12validtools();
//            // DetectorType type = null;
//            int rows = recFtBank.rows();
//            //System.out.println("rows Forward Tracker: ");
//            //  System.out.println(rows);
//            for (int loop = 0; loop < rows; loop++) {
//
//                //int id  = recFtBank.getShort("id", loop);
//                int size = recFtBank.getShort("size", loop);
//                double x = recFtBank.getFloat("x", loop);
//                double y = recFtBank.getFloat("y", loop);
//                double z = recFtBank.getFloat("z", loop);
//                double dx = recFtBank.getFloat("dx", loop);
//                double dy = recFtBank.getFloat("dy", loop);
//                double radius = recFtBank.getFloat("radius", loop);
//                double time = recFtBank.getFloat("time", loop);
//                double energy = recFtBank.getFloat("energy", loop);
//
//                double z0 = 0; // FIXME vertex
//                double path = Math.sqrt(x * x + y * y + (z - z0) * (z - z0));
//                double cx = x / path;
//                double cy = y / path;
//                double cz = (z - z0) / path;
//
//                Response.setPosition(x, y, z);
//                Response.setHitIndex(loop);
//                Response.setEnergy(energy);
//                Response.setTime(time);
//                dataGroups.getItem(4).getH1F("hft_rad").fill(radius);
//                dataGroups.getItem(4).getH1F("hft_time").fill(time);
//                dataGroups.getItem(4).getH1F("hft_energy").fill(energy);
//                dataGroups.getItem(4).getH1F("hft_path").fill(path);
//            }
//            Ft_List.add(Response);
//        }
        
        
    private void loadMap(Map<Integer, List<DetectorResponse>> map, DetectorResponse response) {
        final int iTo = response.getAssociation();
        if (map.containsKey(iTo)) {
            map.get(iTo).add(response);
        } else {
            List<DetectorResponse> iFrom = new ArrayList<>();
            map.put(iTo, iFrom);
            map.get(iTo).add(response);
        }
    }

    private void readEvent(DataEvent de) {
        this.getBanks(de);
        this.readParticles();
        this.readScintillatorResponses();
        this.readCherenkovResponses();
    }

    public List<Particle> getParticles() {
        return particles;
    }
 
    public Map<Integer, List<DetectorResponse>> getFTOFMap() {
        return ftofMap;
    }

    public Map<Integer, List<DetectorResponse>> getCTOFMap() {
        return ctofMap;
    }

    public Map<Integer, List<DetectorResponse>> getCNDMap() {
        return cndMap;
    }

    public Map<Integer, List<DetectorResponse>> getECALMap() {
        return ecalMap;
    }

    public Map<Integer, List<DetectorResponse>> getHTCCMap() {
        return htccMap;
    }

    public Map<Integer, List<DetectorResponse>> getDCTrkMap() {
        return dcMap;
    }

    public Map<Integer, List<DetectorResponse>> getFTMap() {
        return ftMap;
    }

    
    
}
