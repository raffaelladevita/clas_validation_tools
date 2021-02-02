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
