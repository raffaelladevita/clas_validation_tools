/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fizikci0147
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import java.util.HashMap;

import com.sun.prism.Graphics;
import org.jlab.clas.detector.DetectorResponse;
import org.jlab.detector.base.DetectorType;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.math.F1D;

import org.jlab.clas.physics.Particle;
import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.groot.group.DataGroup;
import org.jlab.utils.groups.IndexedList;
import org.jlab.groot.data.TDirectory;
import org.jlab.groot.data.IDataSet;
import org.jlab.analysis.math.ClasMath;
import static org.junit.Assert.*;



public class C12validtools extends DetectorResponse {
    static final boolean debug = false;
    int nEvents = 0;
    int nFtPhotons = 0;
    int nFtElectrons = 0;
    int nFtFd = 0;
    int nHadronsSector[] = {0, 0, 0, 0, 0, 0};
    int hadronPDG = 0;
    int ftPDG = 0;

    private String name = null;
    DataBank mcBank = null, ctrkBank = null, calBank = null, ctofBank = null;
    DataBank trkBank = null, tofBank = null, htccBank = null, ltccBank = null;
    DataBank recPartBank = null, recFtPartBank = null, recTrkBank = null, recFtBank = null;
    DataBank recCalBank = null, recSciBank = null, recCheBank = null;
    DataBank ftcBank = null, fthBank = null, ftpartBank = null, recBank = null, runBank = null;

    Map<Integer, List<Integer>> recCalMap = new HashMap<Integer, List<Integer>>();
    Map<Integer, List<Integer>> recCheMap = new HashMap<Integer, List<Integer>>();
    Map<Integer, List<Integer>> recSciMap = new HashMap<Integer, List<Integer>>();
    Map<Integer, List<Integer>> recTrkMap = new HashMap<Integer, List<Integer>>();
    Map<Integer, List<Integer>> recFtMap = new HashMap<Integer, List<Integer>>();

    IndexedList<DataGroup> dataGroups = new IndexedList<DataGroup>(1);
    EmbeddedCanvasTabbed canvasTabbed = null;
    ArrayList<String> canvasTabNames = new ArrayList<String>();
    ArrayList<Double> REC_Data = new ArrayList<Double>();
    ArrayList<Particle> REC_DataArray = new ArrayList<>();
    List<DetectorResponse> Scint_List = new ArrayList<>();
    List<DetectorResponse> Calo_List = new ArrayList<>();
    List<DetectorResponse> Ft_List = new ArrayList<>();
    List<DetectorResponse> Cher_List = new ArrayList<>();
    String[][] dethistname ={{"hi_p_pos","h_px","hvert_t"},
            {"hsc_energy"},{"hcal_energy","hcal_path"},
            {"hcher_nphe","hcher_time","hcher_phi","hcher_nphe"},
            {"hft_rad","hft_time","hft_energy","hft_path"}};
    public static void main(String[] args) {
        C12validtools c12vt = new C12validtools();
        c12vt.setAnalysisTabNames("REC Particle", "Scintillator", "Calorimeter", "Cherenkov", "Forward Tagger");
        c12vt.CreateHistos();
        String fileName = "/Users/fizikci0147/work/clas_work/clas_validation_tools/small.hipo";
        File file = new File(fileName);
        if (!file.exists() || file.isDirectory()) {
            System.err.println("Cannot find input file.");
        }

        HipoDataSource reader = new HipoDataSource();
        reader.open(fileName);

        while (reader.hasEvent()) {
            DataEvent event = reader.getNextEvent();
            c12vt.getBanks(event);
            c12vt.ProcessEvent(event);
        }
        reader.close();
        //create a jframe
        JFrame frame = new JFrame("C12Validtools");
        frame.setSize(1200, 800);
        frame.add(c12vt.canvasTabbed);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //plot histograms
        c12vt.plotHistos();
       // c12vt.writeDataGroup();
        c12vt.saveHistos("test_histo");
        c12vt.readHistos("test_histo");
        c12vt.checkResultsFT();

    }

    private void plotHistos() {
        /* plotting ....*/
        //REC tab
        canvasTabbed.getCanvas("REC Particle").divide(2, 2);
        canvasTabbed.getCanvas("REC Particle").setGridX(false);
        canvasTabbed.getCanvas("REC Particle").setGridY(false);
        canvasTabbed.getCanvas("REC Particle").cd(0);
        canvasTabbed.getCanvas("REC Particle").draw(dataGroups.getItem(0).getH1F("hi_p_pos"));
        canvasTabbed.getCanvas("REC Particle").cd(1);
        canvasTabbed.getCanvas("REC Particle").draw(dataGroups.getItem(0).getH1F("h_px"));
        canvasTabbed.getCanvas("REC Particle").cd(2);
        canvasTabbed.getCanvas("REC Particle").draw(dataGroups.getItem(0).getH1F("hvert_t"));
        //Scinttilator tab
        canvasTabbed.getCanvas("Scintillator").divide(2, 2);
        canvasTabbed.getCanvas("Scintillator").setGridX(false);
        canvasTabbed.getCanvas("Scintillator").setGridY(false);
        canvasTabbed.getCanvas("Scintillator").cd(0);
        canvasTabbed.getCanvas("Scintillator").draw(dataGroups.getItem(1).getH1F("hsc_energy"));
        //Calorimeter tab
        canvasTabbed.getCanvas("Calorimeter").divide(2, 2);
        canvasTabbed.getCanvas("Calorimeter").setGridX(false);
        canvasTabbed.getCanvas("Calorimeter").setGridY(false);
        canvasTabbed.getCanvas("Calorimeter").cd(0);
        canvasTabbed.getCanvas("Calorimeter").draw(dataGroups.getItem(2).getH1F("hcal_energy"));
        canvasTabbed.getCanvas("Calorimeter").cd(1);
        canvasTabbed.getCanvas("Calorimeter").draw(dataGroups.getItem(2).getH1F("hcal_path"));
        //Cherenkov tab
        canvasTabbed.getCanvas("Cherenkov").divide(2, 2);
        canvasTabbed.getCanvas("Cherenkov").setGridX(false);
        canvasTabbed.getCanvas("Cherenkov").setGridY(false);
        canvasTabbed.getCanvas("Cherenkov").cd(0);
        canvasTabbed.getCanvas("Cherenkov").draw(dataGroups.getItem(3).getH1F("hcher_nphe"));
        canvasTabbed.getCanvas("Cherenkov").cd(1);
        canvasTabbed.getCanvas("Cherenkov").draw(dataGroups.getItem(3).getH1F("hcher_time"));
        canvasTabbed.getCanvas("Cherenkov").cd(2);
        canvasTabbed.getCanvas("Cherenkov").draw(dataGroups.getItem(3).getH1F("hcher_phi"));
        canvasTabbed.getCanvas("Cherenkov").cd(3);
        canvasTabbed.getCanvas("Cherenkov").draw(dataGroups.getItem(3).getH1F("hcher_theta"));
        //Forward Tagger tab
        canvasTabbed.getCanvas("Forward Tagger").divide(2, 2);
        canvasTabbed.getCanvas("Forward Tagger").setGridX(false);
        canvasTabbed.getCanvas("Forward Tagger").setGridY(false);
        canvasTabbed.getCanvas("Forward Tagger").cd(0);
        canvasTabbed.getCanvas("Forward Tagger").draw(dataGroups.getItem(4).getH1F("hft_rad"));
        canvasTabbed.getCanvas("Forward Tagger").cd(1);
        canvasTabbed.getCanvas("Forward Tagger").draw(dataGroups.getItem(4).getH1F("hft_time"));
        canvasTabbed.getCanvas("Forward Tagger").cd(2);
        canvasTabbed.getCanvas("Forward Tagger").draw(dataGroups.getItem(4).getH1F("hft_energy"));
        canvasTabbed.getCanvas("Forward Tagger").cd(3);
        canvasTabbed.getCanvas("Forward Tagger").draw(dataGroups.getItem(4).getH1F("hft_path"));

        canvasTabbed.getCanvas("Cherenkov").draw(dataGroups.getItem(3).getH1F("hcher_nphe"));
        F1D tmp = new F1D("tmp");
        tmp = Cherenkov_Fit(dataGroups.getItem(3).getH1F("hcher_nphe"));
        canvasTabbed.getCanvas("Cherenkov").draw(tmp, "same");

    }

    /*creating Histos*/
    private void CreateHistos() {
        //REC Particle histos
        H1F hi_p_pos = new H1F("hi_p_pos", "hi_p_pos", 100, 0.0, 8.0);
        hi_p_pos.setTitleX("p (GeV)");
        hi_p_pos.setTitleY("Counts");
        H1F hvert_x = new H1F("h_px", "h_px", 100, -0.1, 0.1);
        hvert_x.setTitleX("Px");
        hvert_x.setTitleY("Counts");
        H1F hvert_t = new H1F("hvert_t", "hvert_t", 500, 120, 140);
        hvert_t.setTitleX("Vt");
        hvert_t.setTitleY("Counts");
        H1F beta = new H1F("Beta", "Beta", 100, -1, 3);
        beta.setTitleX("Beta");
        beta.setTitleY("Counts");
        DataGroup dg_pos = new DataGroup(1, 1);
        dg_pos.addDataSet(hi_p_pos, 0);
        dg_pos.addDataSet(hvert_x, 1);
        dg_pos.addDataSet(hvert_t, 2);
        dg_pos.addDataSet(beta, 3);
        dataGroups.add(dg_pos, 0);
        //Scintillator histos
        H1F hsc_energy = new H1F("hsc_energy", "hsc_energy", 100, 0.0, 8.0);
        hsc_energy.setTitleX("Energy");
        hsc_energy.setTitleY("Counts");
        DataGroup dscinth = new DataGroup(1, 1);
        dscinth.addDataSet(hsc_energy, 0);
        dataGroups.add(dscinth, 1);
        //Calorimeter hists
        H1F hcal_energy = new H1F("hcal_energy", "hcal_energy", 100, 0, 8.0);
        hcal_energy.setTitleX("Energy");
        hcal_energy.setTitleY("Counts");
        H1F hcal_path = new H1F("hcal_path", "hcal_path", 100, 500.0, 900.0);
        hcal_path.setTitleX("Path");
        hcal_path.setTitleY("Counts");
        DataGroup dscalh = new DataGroup(1, 1);
        dscalh.addDataSet(hcal_energy, 0);
        dscalh.addDataSet(hcal_path, 1);
        dataGroups.add(dscalh, 2);
        //Cherenkov hists
        H1F hcher_nphe = new H1F("hcher_nphe", "hcher_nphe", 100, 0, 50.0);
        hcher_nphe.setTitleX("nphe");
        hcher_nphe.setTitleY("Counts");
        H1F hcher_time = new H1F("hcher_time", "hcher_time", 100, 120.0, 130.0);
        hcher_time.setTitleX("Time");
        hcher_time.setTitleY("Counts");
        H1F hcher_phi = new H1F("hcher_phi", "hcher_phi", 100, -10., 10.);
        hcher_phi.setTitleX("Phi");
        hcher_phi.setTitleY("Counts");
        H1F hcher_theta = new H1F("hcher_theta", "hcher_theta", 100, -3., 3.);
        hcher_theta.setTitleX("#theta");
        hcher_theta.setTitleY("Counts");
        DataGroup dscher = new DataGroup(1, 1);
        dscher.addDataSet(hcher_nphe, 0);
        dscher.addDataSet(hcher_time, 1);
        dscher.addDataSet(hcher_phi, 2);
        dscher.addDataSet(hcher_theta, 3);
        dataGroups.add(dscher, 3);
        //Forward Tagger hists
        H1F hft_rad = new H1F("hft_rad", "hft_rad", 100, 0, 10.0);
        hft_rad.setTitleX("Radius");
        hft_rad.setTitleY("Counts");
        H1F hft_time = new H1F("hft_time", "hft_time", 100, 120.0, 150.0);
        hft_time.setTitleX("Time");
        hft_time.setTitleY("Counts");
        H1F hft_energy = new H1F("hft_energy", "hft_energy", 100, 0., 20.);
        hft_energy.setTitleX("Energy");
        hft_energy.setTitleY("Counts");
        H1F hft_path = new H1F("hft_path", "hft_path", 100, 100., 300.);
        hft_path.setTitleX("Path");
        hft_path.setTitleY("Counts");
        DataGroup dsft = new DataGroup(1, 1);
        dsft.addDataSet(hft_rad, 0);
        dsft.addDataSet(hft_time, 1);
        dsft.addDataSet(hft_energy, 2);
        dsft.addDataSet(hft_path, 3);
        dataGroups.add(dsft, 4);

    }

    public void setAnalysisTabNames(String... names) {
        for (String name : names) {
            canvasTabNames.add(name);
        }
        canvasTabbed = new EmbeddedCanvasTabbed(names);
    }

    private DataBank getBank(DataEvent de, String bankName) {
        DataBank bank = null;
        if (de.hasBank(bankName)) {
            bank = de.getBank(bankName);
        }
        return bank;
    }

    private void getBanks(DataEvent de) {
        ctrkBank = getBank(de, "CVTRec::Tracks");
        tofBank = getBank(de, "FTOF::clusters");
        trkBank = getBank(de, "TimeBasedTrkg::TBTracks");
        recPartBank = getBank(de, "REC::Particle");
        recFtPartBank = getBank(de, "RECFT::Particle");
        mcBank = getBank(de, "MC::Particle");
        recCheBank = getBank(de, "REC::Cherenkov");
        recCalBank = getBank(de, "REC::Calorimeter");
        recSciBank = getBank(de, "REC::Scintillator");
        ltccBank = getBank(de, "LTCC::clusters");
        htccBank = getBank(de, "HTCC::rec");
        recTrkBank = getBank(de, "REC::Track");
        recFtBank = getBank(de, "REC::ForwardTagger");
        ftcBank = getBank(de, "FTCAL::clusters");
        fthBank = getBank(de, "FTHODO::clusters");
        ftpartBank = getBank(de, "FT::particles");
        calBank = getBank(de, "ECAL::clusters");
        ctofBank = getBank(de, "CTOF::hits");
        recBank = getBank(de, "REC::Event");
        runBank = getBank(de, "RUN::config");
        loadMaps();
    }

    public void loadMap(Map<Integer, List<Integer>> map, DataBank fromBank, DataBank toBank, String idxVarName) {
        map.clear();
        if (fromBank == null) return;
        if (toBank == null) return;
        for (int ii = 0; ii < fromBank.rows(); ii++) {
            final int iTo = fromBank.getInt(idxVarName, ii);
            if (map.containsKey(iTo)) {
                map.get(iTo).add(ii);
            } else {
                List<Integer> iFrom = new ArrayList<Integer>();
                map.put(iTo, iFrom);
                map.get(iTo).add(ii);
            }
        }
    }

    /**
     * Load mapping from REC::Particle to REC::"Detector".
     */
    public void loadMaps() {
        loadMap(recCalMap, recCalBank, recPartBank, "pindex");
        loadMap(recCheMap, recCheBank, recPartBank, "pindex");
        loadMap(recSciMap, recSciBank, recPartBank, "pindex");
        loadMap(recTrkMap, recTrkBank, recPartBank, "pindex");
        loadMap(recFtMap, recFtBank, recPartBank, "pindex");
    }


    public DataBank getDetectorBank(int detId) {
        DataBank bankTo = null;
        return null;
    }

    public void Response(int sector, int layer, int component) {
        this.getDescriptor().setSectorLayerComponent(sector, layer, component);
    }

    private void ProcessEvent(DataEvent event) {
        nEvents++;
        //scintillator variables
        //debug
        if (recBank == null || recPartBank == null || recFtBank == null || recFtPartBank == null) return;
        if (debug) {
            System.out.println("\n\n#############################################################\n");
            if (ftpartBank != null) ftpartBank.show();
            recFtBank.show();
            recPartBank.show();
        }
//Rec particle
        if ((nEvents % 10000) == 0) System.out.println("Analyzed " + nEvents + " events");
        if (event.hasBank("REC::Particle") == true) {

            int rows = recPartBank.rows();
            //System.out.println("rows recbank: ");
            // System.out.println(rows);
            for (int loop = 0; loop < rows; loop++) {
                int pidCode = 0;
                if (recPartBank.getByte("charge", loop) == -1) pidCode = 11;
                else if (recPartBank.getByte("charge", loop) == 1) pidCode = 211;
                else pidCode = 22;

                Particle recParticle = new Particle(
                        pidCode,
                        recPartBank.getFloat("px", loop),
                        recPartBank.getFloat("py", loop),
                        recPartBank.getFloat("pz", loop),
                        recPartBank.getFloat("vx", loop),
                        recPartBank.getFloat("vy", loop),
                        recPartBank.getFloat("vz", loop));
                float vert_t = recPartBank.getFloat("vt", loop);
                REC_DataArray.add(recParticle);
                //System.out.println(recParticle.charge());
                dataGroups.getItem(0).getH1F("hi_p_pos").fill(recParticle.p());
                dataGroups.getItem(0).getH1F("h_px").fill(recParticle.px());
                dataGroups.getItem(0).getH1F("hvert_t").fill(vert_t);


            }
        }
//Scintillator
        if (event.hasBank("REC::Scintillator")) {
            C12validtools Response = new C12validtools();
            int rows = recSciBank.rows();
            // System.out.println("rows scint: ");
            // System.out.println(rows);
            for (int loop = 0; loop < rows; loop++) {
                int index = recSciBank.getInt("pindex", loop);
                int layer = recSciBank.getByte("layer", loop);
                int sector = recSciBank.getByte("sector", loop);
                int paddle = recSciBank.getInt("component", loop);
                float energy = recSciBank.getFloat("energy", loop);
                float time = recSciBank.getFloat("time", loop);
                float x = recSciBank.getFloat("x", loop);
                float y = recSciBank.getFloat("y", loop);
                float z = recSciBank.getFloat("z", loop);
                float hx = recSciBank.getFloat("hx", loop);
                float hy = recSciBank.getFloat("hy", loop);
                float hz = recSciBank.getFloat("hz", loop);
                float path = recSciBank.getFloat("path", loop);
                dataGroups.getItem(1).getH1F("hsc_energy").fill(hx);
                Response.setPosition(layer, sector, paddle);
                Response.setEnergy((energy));
                Response.setEnergy((time));

            }
            Scint_List.add(Response);
            //Energy.put(nEvents,energy);
        }
//Calorimeter
        if (event.hasBank("REC::Calorimeter")) {
            C12validtools Response = new C12validtools();
            // DetectorType type = null;
            int rows = recCalBank.rows();
            //  System.out.println("rows Calo: ");
            //  System.out.println(rows);
            for (int loop = 0; loop < rows; loop++) {
                int sector = recCalBank.getByte("sector", loop);
                int layer = recCalBank.getByte("layer", loop);
                float x = recCalBank.getFloat("x", loop);
                float y = recCalBank.getFloat("y", loop);
                float z = recCalBank.getFloat("z", loop);
                float hx = recCalBank.getFloat("hx", loop);
                float hy = recCalBank.getFloat("hy", loop);
                float hz = recCalBank.getFloat("hz", loop);
                float path = recCalBank.getFloat("path", loop);
                float energy = recCalBank.getFloat("energy", loop);
                //float u = recCalBank.getFloat("widthu",loop);
                //float v = recCalBank.getFloat("widthu",loop);
                //float w = recCalBank.getFloat("widthw",loop);
                //Response.getDescriptor().setType(type);
                Response.setPosition(x, y, z);
                Response.setHitIndex(loop);
                Response.setEnergy(energy);
                Response.setTime(recCalBank.getFloat("time", loop));
                Response.setStatus(recCalBank.getInt("status", loop));
                dataGroups.getItem(2).getH1F("hcal_energy").fill(energy);
                dataGroups.getItem(2).getH1F("hcal_path").fill(path);
            }
            Calo_List.add(Response);
        }
//CHERENKOV

        DetectorType type = null;
        if (event.hasBank("REC::Cherenkov")) {
            C12validtools Response = new C12validtools();
            // DetectorType type = null;
            int rows = recCheBank.rows();
            //  System.out.println("rows Cherenkov: ");
            //  System.out.println(rows);
            for (int loop = 0; loop < rows; loop++) {
                float x = recCheBank.getFloat("x", loop);
                float y = recCheBank.getFloat("y", loop);
                float z = recCheBank.getFloat("z", loop);
                //float hx = recCheBank.getFloat("hx", loop);
                //float hy = recCheBank.getFloat("hy", loop);
                //float hz = recCheBank.getFloat("hz", loop);
                double time = recCheBank.getFloat("time", loop);
                double nphe = recCheBank.getFloat("nphe", loop);
                double theta = Math.atan2(Math.sqrt(x * x + y * y), z);
                double phi = Math.atan2(y, x);
                int sector = 0;

                double dtheta = 0, dphi = 0;
                if (type == DetectorType.HTCC) {
                    dtheta = 10 * 3.14159 / 180; // based on MC
                    dphi = 18 * 3.14159 / 180; // based on MC
                    // HTCC reconstruction does not provide a sector,
                    // so we calculate it based on hit position:
                    sector = DetectorResponse.getSector(phi);
                } else if (type == DetectorType.LTCC) {
                    dtheta = (35 - 5) / 18 * 2 * 3.14159 / 180; // +/- 2 mirrors
                    dphi = 10 * 3.14159 / 180;
                    sector = recCheBank.getByte("sector", loop);
                }
                Response.setTime(time);
                Response.setPosition(x, y, z);

                dataGroups.getItem(3).getH1F("hcher_nphe").fill(nphe);
                dataGroups.getItem(3).getH1F("hcher_time").fill(time);
                dataGroups.getItem(3).getH1F("hcher_phi").fill(phi);
                dataGroups.getItem(3).getH1F("hcher_theta").fill(theta);
            }
            Cher_List.add(Response);
        }
// FORWARD TAGGER
        if (event.hasBank("REC::ForwardTagger")) {
            C12validtools Response = new C12validtools();
            // DetectorType type = null;
            int rows = recFtBank.rows();
            //System.out.println("rows Forward Tracker: ");
            //  System.out.println(rows);
            for (int loop = 0; loop < rows; loop++) {

                //int id  = recFtBank.getShort("id", loop);
                int size = recFtBank.getShort("size", loop);
                double x = recFtBank.getFloat("x", loop);
                double y = recFtBank.getFloat("y", loop);
                double z = recFtBank.getFloat("z", loop);
                double dx = recFtBank.getFloat("dx", loop);
                double dy = recFtBank.getFloat("dy", loop);
                double radius = recFtBank.getFloat("radius", loop);
                double time = recFtBank.getFloat("time", loop);
                double energy = recFtBank.getFloat("energy", loop);

                double z0 = 0; // FIXME vertex
                double path = Math.sqrt(x * x + y * y + (z - z0) * (z - z0));
                double cx = x / path;
                double cy = y / path;
                double cz = (z - z0) / path;

                Response.setPosition(x, y, z);
                Response.setHitIndex(loop);
                Response.setEnergy(energy);
                Response.setTime(time);
                dataGroups.getItem(4).getH1F("hft_rad").fill(radius);
                dataGroups.getItem(4).getH1F("hft_time").fill(time);
                dataGroups.getItem(4).getH1F("hft_energy").fill(energy);
                dataGroups.getItem(4).getH1F("hft_path").fill(path);
            }
            Ft_List.add(Response);
        }

    }

    public void Read_RECArray(ArrayList<Particle> Data) {

        System.out.println(Data.get(0));
        System.out.println(Data.get(1));
        System.out.println(Data.size());
    }

    public F1D Cherenkov_Fit(H1F histo) {
        System.out.println(histo.getName());
        double tmp_Mean = histo.getMean();
        int Max_Bin = histo.getMaximumBin();
        double tmp_Amp = histo.getBinContent(Max_Bin);
        double tmp_sigma = histo.getRMS();
        //System.out.println(tmp_Amp);
        F1D f1 = new F1D("f1", "[amp]*gaus(x,[mean],[sigma])", 0, 50.0);
        DataFitter.fit(f1, histo, "Q");
        f1.setParameter(0, tmp_Amp);
        f1.setParameter(1, tmp_Mean);
        f1.setParameter(2, tmp_sigma / 2);
        f1.setLineColor(5);
        f1.setLineWidth(7);
        f1.setOptStat(111110);
        return f1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String getPrefix(IDataSet ds) {
        String prefix = ds.getName().split("_")[0];
        return prefix;
    }

  /*  public void writeDataGroup() {
        TDirectory dir = new TDirectory();
        String folder = "/" + this.dataGroups.getMap();
        dir.mkdir(folder);
        //Map<Long, DataGroup> map = this.dataGroups.getMap();
       // for(Map.Entry<Long, DataGroup> entry : map.entrySet()){
       //     DataGroup group = entry.getValue();

      //      System.out.println(entry.getValue());
     //   }
     //   System.out.println("detector group : ");
     //   System.out.println(folder);
        for (int k = 1; k < 6; k++) {
            String subfolder = folder + "_" + k;
            dir.mkdir(subfolder);
            dir.cd(subfolder);
            for (int i = 1; i < 5; i++) {
                List<IDataSet> dsList = dataGroups.getItem(k).getData(i);
                for (IDataSet ds : dsList) {
                    System.out.println("\t --> " + ds.getName());
                    dir.addDataSet(ds);
                }
            }
        }
    } */


    public void readHistos(String fileName) {
        System.out.println("Opening file: " + fileName);
        TDirectory dir = new TDirectory();
        dir.readFile(fileName);
        System.out.println(dir.getDirectoryList());
        dir.cd();
        dir.pwd();

    }

    public void saveHistos(String fileName) {
        TDirectory dir = new TDirectory();
        for (int i = 0; i < dethistname.length; ++i) {
            for (int j = 0; j < dethistname[i].length; ++j) {
                dir.addDataSet(dataGroups.getItem(i).getH1F(dethistname[i][j]));
                //System.out.println("Saving histograms to file " + dethistname[i][j]);
            }
        }
        System.out.println("Saving histograms to file " + fileName);
        dir.writeFile(fileName);
    }

    private void checkResultsFT() {
        double npe = dataGroups.getItem(3).getH1F("hcher_nphe").integral();
        final double npe_ave = npe / nEvents;
        System.out.println("\n#############################################################");
        System.out.println(String.format("npe/Events = %.3f", npe_ave));;
        System.out.println("#############################################################");
        assertEquals(npe_ave>0.15,false);

    }
}