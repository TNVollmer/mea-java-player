package thkoeln.dungeon.monte.game.application;


import thkoeln.dungeon.monte.core.AbstractDungeonMockingIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import thkoeln.dungeon.monte.DungeonPlayerConfiguration;
import thkoeln.dungeon.monte.game.domain.Game;
import thkoeln.dungeon.monte.game.domain.GameRepository;
import thkoeln.dungeon.monte.game.domain.GameStatus;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext
@SpringBootTest( classes = DungeonPlayerConfiguration.class )
public class GameApplicationServiceIntegrationTest extends AbstractDungeonMockingIntegrationTest {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameApplicationService gameApplicationService;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        gameRepository.deleteAll();
    }


    @Test
    public void testRunningGameAvailable() throws Exception {
        // given
        mockGamesGetWithRunning();

        // when
        gameApplicationService.fetchRemoteGame();

        // then
        Game game = gameApplicationService.queryActiveGame();
        assertNotNull( game );
        assertEquals( GameStatus.RUNNING, game.getGameStatus() );
    }

    @Test
    public void testCreatedGameAvailable() throws Exception {
        // given
        mockGamesGetWithCreated();

        // when
        gameApplicationService.fetchRemoteGame();

        // then
        Game game = gameApplicationService.queryActiveGame();
        assertNotNull( game );
        assertEquals( GameStatus.CREATED, game.getGameStatus() );
    }
}