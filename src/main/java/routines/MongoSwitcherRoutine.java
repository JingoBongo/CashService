package routines;

import main.MainRunner;

import java.util.TimerTask;

public class MongoSwitcherRoutine extends TimerTask {


    @Override
    public void run(){
        //this one is checking if we are using default DB. every 15 minutes. if not, try if default can be connected, takes data from secondary and puts into default

        //checking if we are using default
        if(MainRunner.dbIsSwitched){
//            boolean oldDbIsBack = MongoHandler.checkDbIsAvailable(MainRunner.previousPort);
//            if(oldDbIsBack){ // if its back, change port to normal
//                MainRunner.dbPort = MainRunner.previousPort;
//                // AND RESTORE DATA
////                Runtime.getRuntime().exec("mongoexport --host host_name --port port_number --db myDatabase --collection Page --out Page.json");
//            }
            //TODO this entire section should be remade after Katya brings a wat to import/export collection
        }
    }
}
