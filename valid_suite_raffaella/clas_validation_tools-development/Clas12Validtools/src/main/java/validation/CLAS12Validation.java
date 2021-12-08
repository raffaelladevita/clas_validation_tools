package validation;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import modules.FTOFModule;
import modules.CTOFModule;
import modules.CNDModule;
import modules.HTCCModule;
import modules.LTCCModule;
import modules.ECALModule;
import modules.FTModule;
import modules.EventBuilderModule;

import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;


import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.groot.data.TDirectory;
import org.jlab.jnp.utils.benchmark.ProgressPrintout;
import org.jlab.utils.options.OptionParser;

/**
 *
 * @author fizikci0147
 */


/**
 * TODO:
 * test command line options and verify everything works
 * add modules for EventBuilder, Calorimeter, CTOF, CND, FT, DCTRACKS, CVTTRACKS, importing histograms from here
 * complete in to Event class missing "readResponse" methods
 */

public class CLAS12Validation {
    
    private final boolean debug = false;

    ArrayList<Module>    modules = new ArrayList<>();

    public CLAS12Validation() {
        this.init();
    }
    
    


    /*creating Histos*/
//    private void CreateHistos() {
//        //REC Particle histos
//        H1F hi_p_pos = new H1F("hi_p_pos", "hi_p_pos", 100, 0.0, 8.0);
//        hi_p_pos.setTitleX("p (GeV)");
//        hi_p_pos.setTitleY("Counts");
//        H1F hvert_x = new H1F("h_px", "h_px", 100, -0.1, 0.1);
//        hvert_x.setTitleX("Px");
//        hvert_x.setTitleY("Counts");
//        H1F hvert_t = new H1F("hvert_t", "hvert_t", 500, 120, 140);
//        hvert_t.setTitleX("Vt");
//        hvert_t.setTitleY("Counts");
//        H1F beta = new H1F("Beta", "Beta", 100, -1, 3);
//        beta.setTitleX("Beta");
//        beta.setTitleY("Counts");
//        DataGroup dg_pos = new DataGroup(1, 1);
//        dg_pos.addDataSet(hi_p_pos, 0);
//        dg_pos.addDataSet(hvert_x, 1);
//        dg_pos.addDataSet(hvert_t, 2);
//        dg_pos.addDataSet(beta, 3);
//        dataGroups.add(dg_pos, 0);
//        //Calorimeter hists
//        H1F hcal_energy = new H1F("hcal_energy", "hcal_energy", 100, 0, 8.0);
//        hcal_energy.setTitleX("Energy");
//        hcal_energy.setTitleY("Counts");
//        H1F hcal_path = new H1F("hcal_path", "hcal_path", 100, 500.0, 900.0);
//        hcal_path.setTitleX("Path");
//        hcal_path.setTitleY("Counts");
//        DataGroup dscalh = new DataGroup(1, 1);
//        dscalh.addDataSet(hcal_energy, 0);
//        dscalh.addDataSet(hcal_path, 1);
//        dataGroups.add(dscalh, 2);
//        //Forward Tagger hists
//        H1F hft_rad = new H1F("hft_rad", "hft_rad", 100, 0, 10.0);
//        hft_rad.setTitleX("Radius");
//        hft_rad.setTitleY("Counts");
//        H1F hft_time = new H1F("hft_time", "hft_time", 100, 120.0, 150.0);
//        hft_time.setTitleX("Time");
//        hft_time.setTitleY("Counts");
//        H1F hft_energy = new H1F("hft_energy", "hft_energy", 100, 0., 20.);
//        hft_energy.setTitleX("Energy");
//        hft_energy.setTitleY("Counts");
//        H1F hft_path = new H1F("hft_path", "hft_path", 100, 100., 300.);
//        hft_path.setTitleX("Path");
//        hft_path.setTitleY("Counts");
//        DataGroup dsft = new DataGroup(1, 1);
//        dsft.addDataSet(hft_rad, 0);
//        dsft.addDataSet(hft_time, 1);
//        dsft.addDataSet(hft_energy, 2);
//        dsft.addDataSet(hft_path, 3);
//        dataGroups.add(dsft, 4);
//
//    }

    private void init() {
        this.modules.add(new FTOFModule());
        this.modules.add(new CTOFModule());
        this.modules.add(new CNDModule());
        this.modules.add(new HTCCModule());
        this.modules.add(new LTCCModule());
        this.modules.add(new ECALModule());
        this.modules.add(new FTModule());
        this.modules.add(new EventBuilderModule());

    }

    private void processEvent(DataEvent de) {
        Event event = new Event(de);
        
        for(Module m : modules) m.processEvent(event);
    }

    private void analyzeHistos() {
        for(Module m : modules) m.analyzeHistos();
    }

    private EmbeddedCanvasTabbed plotHistos() {
        EmbeddedCanvasTabbed canvas  = null;
        String cname = null;
        for(int i=0; i<modules.size(); i++) {
            Module m = modules.get(i);
            m.analyzeHistos();
            cname = m.getName();
            if(canvas==null) canvas = new EmbeddedCanvasTabbed(cname);
            else             canvas.addCanvas(cname);
            canvas.getCanvas(cname).draw(m.getHistos());

            System.out.println("Saving histograms to file " + cname);

        }
        return canvas;
    }

    public void readHistos(String fileName) {
        System.out.println("Opening file: " + fileName);
        TDirectory dir = new TDirectory();
        dir.readFile(fileName);
        System.out.println(dir.getDirectoryList());
        dir.cd();
        dir.pwd();
        for(Module m : modules) {
            m.readDataGroup(dir);
        }
    }

    public void saveHistos(String fileName) {
        TDirectory dir = new TDirectory();
        for(Module m : modules) {
            m.writeDataGroup(dir);
        }
        System.out.println("Saving histograms to file " + fileName);
        dir.writeFile(fileName);
    }

    private void testHistos() {
        for(Module m : modules) {
            m.testHistos();
        }
    }
        public static void main(String[] args) {
        
        OptionParser parser = new OptionParser("clas12Validation");
        parser.setRequiresInputList(false);
        // valid options for event-base analysis
        parser.addOption("-o"          ,"",     "output file name prefix");
        parser.addOption("-n"          ,"-1",   "maximum number of events to process");
        // histogram based analysis
        parser.addOption("-histo"      ,"0",    "read histogram file (0/1)");
        parser.addOption("-plot"       ,"1",    "display histograms (0/1)");
        parser.addOption("-stats"      ,"",     "histogram stat option");
        parser.addOption("-threshold"  ,"0",    "minimum number of entries for histogram differences");
        
        parser.parse(args);
        
        String namePrefix  = parser.getOption("-o").stringValue();        
        String histoName   = "histo.hipo";
        if(!namePrefix.isEmpty()) {
            histoName  = namePrefix + "_" + histoName; 
        }
        int     maxEvents    = parser.getOption("-n").intValue();        
        boolean readHistos   = (parser.getOption("-histo").intValue()!=0);            
        boolean openWindow   = (parser.getOption("-plot").intValue()!=0);
        String  optStats     = parser.getOption("-stats").stringValue(); 
        
        if(!openWindow) System.setProperty("java.awt.headless", "true");

        CLAS12Validation c12vt = new CLAS12Validation();
        
        List<String> inputList = parser.getInputList();
        if(inputList.isEmpty()==true){
            parser.printUsage();
            System.out.println("\n >>>> error: no input file is specified....\n");
            System.exit(0);
        }

        if(readHistos) {
            c12vt.readHistos(inputList.get(0));
        }
        else{

            ProgressPrintout progress = new ProgressPrintout();

            int counter = -1;
            for(String inputFile : inputList){
                HipoDataSource reader = new HipoDataSource();
                reader.open(inputFile);

                
                while (reader.hasEvent()) {

                    counter++;

                    DataEvent event = reader.getNextEvent();
                    c12vt.processEvent(event);
                    
                    progress.updateStatus();
                    if(maxEvents>0){
                        if(counter>=maxEvents) break;
                    }
                }
                progress.showStatus();
                reader.close();
            }    
            c12vt.analyzeHistos();
            c12vt.testHistos();
            c12vt.saveHistos(histoName);
        }

        if(openWindow) {
            JFrame frame = new JFrame("CLAS12Validation");
            EmbeddedCanvasTabbed canvas = null;
            canvas = c12vt.plotHistos();
            frame.setSize(1200, 800);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }

}