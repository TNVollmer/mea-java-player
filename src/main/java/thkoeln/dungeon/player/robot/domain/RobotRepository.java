package thkoeln.dungeon.player.robot.domain;

import org.springframework.data.repository.CrudRepository;
import thkoeln.dungeon.player.core.domainprimitives.robot.RobotType;
import thkoeln.dungeon.player.planet.domain.Planet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RobotRepository extends CrudRepository<Robot, UUID> {
    Optional<Robot> findByRobotId(UUID id);
    List<Robot> findByRobotType(RobotType type);
    List<Robot> findByPlanet(Planet planet);
    Integer countAllByRobotType(RobotType type);
    Integer countBy();
}
