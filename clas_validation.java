import java.io.File;
//import org.junit.Test;
import java.util.ArrayList;
import javax.swing.JFrame;
//import static org.junit.Assert.*;

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
    
public class ReadInput {


    String resultDir=System.getProperty("RESULTS");
        File dir = new File(resultDir);
        if (!dir.isDirectory()) {
            System.err.println("Cannot find output directory");
//            assertEquals(false, true);
        }
        String inname = System.getProperty("INPUTFILE");
        String fileName=resultDir + "/out_" + inname + ".hipo";
        File file = new File(fileName);
        if (!file.exists() || file.isDirectory()) {
            System.err.println("Cannot find input file.");
//            assertEquals(false, true); // use method from org.junit.Assert.* library to be able to do quantitative checks and test specific conditions
        }

        HipoDataSource reader = new HipoDataSource();
        reader.open(fileName);

        while (reader.hasEvent()) {
	    DataEvent event = reader.getNextEvent();
            ttest.processEvent(event);
        }
        reader.close();


}

public class TrackingTest {
        
    private DataBank getBank(DataEvent de,String bankName) {
        DataBank bank=null;
        if (de.hasBank(bankName))
            bank=de.getBank(bankName);
        return bank;
    }

    private void getBanks(DataEvent de) {
        recBank     = getBank(de,"REC::Event");
        runBank     = getBank(de,"RUN::config");
        loadMaps();
    }

    public void loadMap(Map<Integer,List<Integer>> map, 
                        DataBank fromBank, 
                        DataBank toBank, 
                        String idxVarName) {
        map.clear();
        if (fromBank==null) return;
        if (toBank==null) return;
        for (int ii=0; ii<fromBank.rows(); ii++) {
            final int iTo=fromBank.getInt(idxVarName,ii);
            if (map.containsKey(iTo)) {
                map.get(iTo).add(ii);
            }
            else {
                List<Integer> iFrom=new ArrayList<Integer>();
                map.put(iTo,iFrom);
                map.get(iTo).add(ii);
            }
        }
    }

        /**
     *
     * Load mapping from REC::Particle to REC::"Detector".
     *
     */
    public void loadMaps() {
        loadMap(recCalMap,recCalBank,recPartBank,"pindex");
        loadMap(recCheMap,recCheBank,recPartBank,"pindex");
        loadMap(recSciMap,recSciBank,recPartBank,"pindex");
        loadMap(recTrkMap,recTrkBank,recPartBank,"pindex");
    }


    public DataBank getDetectorBank(int detId) {
        DataBank bankTo=null;
        if (detId == DetectorType.DC.getDetectorId())
            bankTo = trkBank;
        else if (detId == DetectorType.CVT.getDetectorId())
            bankTo = ctrkBank;
        else if (detId == DetectorType.ECAL.getDetectorId())
            bankTo = calBank;
        else if (detId == DetectorType.FTOF.getDetectorId())
            bankTo = tofBank;
        else if (detId == DetectorType.HTCC.getDetectorId())
            bankTo = htccBank;
        else if (detId == DetectorType.LTCC.getDetectorId())
            bankTo = ltccBank;
        else if (detId == DetectorType.FTCAL.getDetectorId())
            bankTo = ftcBank;
        else if (detId == DetectorType.FTHODO.getDetectorId())
            bankTo = fthBank;
        else
            throw new RuntimeException("Unkown detector Id:  "+detId);
        return bankTo;
    }



    private void ProcessEvent(DataEvent event){

    }



}
