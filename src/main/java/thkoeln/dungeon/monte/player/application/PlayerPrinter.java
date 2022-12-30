package thkoeln.dungeon.monte.player.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.dungeon.monte.domainprimitives.Coordinate;
import thkoeln.dungeon.monte.domainprimitives.TwoDimDynamicArray;
import thkoeln.dungeon.monte.game.application.GamePrinter;
import thkoeln.dungeon.monte.planet.application.PlanetApplicationService;
import thkoeln.dungeon.monte.planet.domain.Planet;
import thkoeln.dungeon.monte.robot.application.RobotPrinter;
import thkoeln.dungeon.monte.util.AbstractPrinter;

import java.util.Map;

/**
 * Printer class to output the current player status to console.
 */
@Service
public class PlayerPrinter extends AbstractPrinter {
    private GamePrinter gamePrinter;
    private RobotPrinter robotPrinter;
    private MapPrinter mapPrinter;

    @Autowired
    public PlayerPrinter( GamePrinter gamePrinter,
                          RobotPrinter robotPrinter,
                          MapPrinter mapPrinter ) {
        this.gamePrinter = gamePrinter;
        this.robotPrinter = robotPrinter;
        this.mapPrinter = mapPrinter;
    }


    /**
     * @return Print the complete player status formatted for the console.
     */

    public void printStatus() {
        initializeOutput();
        write( RED );
        gamePrinter.printStatus();
        robotPrinter.printRobotList();
        mapPrinter.printMap();
        write( RESET );
        flush();
    }

}
