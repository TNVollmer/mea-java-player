package thkoeln.dungeon.player.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import thkoeln.dungeon.player.game.domain.Game;
import thkoeln.dungeon.player.game.domain.GameStatus;
import thkoeln.dungeon.player.core.restadapter.GameDto;
import thkoeln.dungeon.player.core.restadapter.PlayerRegistryDto;

import java.net.URI;
import java.util.UUID;
import thkoeln.dungeon.player.player.application.PlayerExternalEventListener;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
public class AbstractDungeonMockingIntegrationTest {
    @Value("${GAME_SERVICE:http://localhost:8080}")
    protected String gameServiceURIString;
    protected URI playersGetURI;
    protected URI playersPostURI;
    @Value("${dungeon.playerName}")
    protected String playerName;
    @Value("${dungeon.playerEmail}")
    protected String playerEmail;

    protected URI gamesURI;
    protected Game game;
    protected UUID openGameId;
    protected GameDto[] gameDtosWithCreatedGame;
    protected GameDto[] gameDtosWithRunningGame;

    @Autowired
    protected RestTemplate restTemplate;
    protected MockRestServiceServer mockServer;
    protected ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    protected ModelMapper modelMapper = new ModelMapper();

    protected final UUID genericEventId = UUID.randomUUID();
    protected final String genericEventIdStr = genericEventId.toString();
    protected final UUID genericTransactionId = UUID.randomUUID();
    protected final String genericTransactionIdStr = genericTransactionId.toString();
    protected final UUID playerId = UUID.randomUUID();
    protected PlayerRegistryDto playerRegistryDto;

    // Mock the listener away.
    // TODO: Start a testcontainer
    @MockBean
    PlayerExternalEventListener eventListener;

    protected void setUp() throws Exception {
        String getExtension = "/players?name=" + playerName + "&mail=" + playerEmail;
        playersGetURI = new URI( gameServiceURIString + getExtension );
        playersPostURI = new URI( gameServiceURIString + "/players" );
        playerRegistryDto = new PlayerRegistryDto();
        playerRegistryDto.setPlayerExchange( "test-" + playerName );

        gamesURI = new URI( gameServiceURIString + "/games" );
        createMockGameDtos();
        resetMockServer();
    }

    protected void resetMockServer() {
        mockServer = MockRestServiceServer.bindTo( restTemplate ).ignoreExpectOrder( true ).build();
    }

    protected void createMockGameDtos() {
        gameDtosWithRunningGame = new GameDto[2];
        gameDtosWithRunningGame[0] = new GameDto();
        gameDtosWithRunningGame[0].setGameStatus( GameStatus.ENDED );
        gameDtosWithRunningGame[0].setGameId( UUID.randomUUID() );
        gameDtosWithRunningGame[0].setCurrentRoundNumber( 100 );
        gameDtosWithRunningGame[1] = new GameDto();
        gameDtosWithRunningGame[1].setGameStatus( GameStatus.STARTED );
        gameDtosWithRunningGame[1].setGameId( UUID.randomUUID() );
        gameDtosWithRunningGame[1].setCurrentRoundNumber( 34 );

        gameDtosWithCreatedGame = new GameDto[2];
        gameDtosWithCreatedGame[0] = gameDtosWithRunningGame[0];
        gameDtosWithCreatedGame[1] = new GameDto();
        gameDtosWithCreatedGame[1].setGameStatus( GameStatus.CREATED );
        gameDtosWithCreatedGame[1].setGameId( UUID.randomUUID() );
        gameDtosWithCreatedGame[1].setCurrentRoundNumber( 0 );
    }


    protected void mockPlayerPost() throws Exception {
        PlayerRegistryDto responseDto = playerRegistryDto.clone();
        responseDto.setPlayerId( playerId );
        mockServer.expect( ExpectedCount.once(), MockRestRequestMatchers.requestTo( playersPostURI ) )
                .andExpect( MockRestRequestMatchers.method(HttpMethod.POST) )
                .andRespond( MockRestResponseCreators.withSuccess(objectMapper.writeValueAsString( responseDto ), MediaType.APPLICATION_JSON) );
    }


    protected void mockPlayerGetNotFound() throws Exception {
        mockServer.expect( ExpectedCount.once(), MockRestRequestMatchers.requestTo(playersGetURI) )
                .andExpect( MockRestRequestMatchers.method(HttpMethod.GET) )
                .andRespond( MockRestResponseCreators.withStatus( HttpStatus.NOT_FOUND ) );
    }


    protected void mockPlayerGetFound() throws Exception {
        PlayerRegistryDto responseDto = playerRegistryDto.clone();
        responseDto.setPlayerId( playerId );
        mockServer.expect( ExpectedCount.once(), MockRestRequestMatchers.requestTo(playersGetURI) )
                .andExpect( MockRestRequestMatchers.method(HttpMethod.GET) )
                .andRespond( MockRestResponseCreators.withSuccess(objectMapper.writeValueAsString(responseDto), MediaType.APPLICATION_JSON) );
    }


    protected void mockGamesGetWithRunning() throws Exception {
        openGameId = null;
        mockServer.expect( ExpectedCount.once(), MockRestRequestMatchers.requestTo( gamesURI ) )
                .andExpect( MockRestRequestMatchers.method(HttpMethod.GET) )
                .andRespond( MockRestResponseCreators.withSuccess(objectMapper.writeValueAsString(gameDtosWithRunningGame), MediaType.APPLICATION_JSON) );
    }



    protected void mockGamesGetWithCreated() throws Exception {
        openGameId = gameDtosWithCreatedGame[1].getGameId();
        mockServer.expect( ExpectedCount.once(), MockRestRequestMatchers.requestTo( gamesURI ) )
                .andExpect( MockRestRequestMatchers.method(HttpMethod.GET) )
                .andRespond( MockRestResponseCreators.withSuccess(objectMapper.writeValueAsString(gameDtosWithCreatedGame), MediaType.APPLICATION_JSON) );
    }


    protected void mockRegistrationEndpointFor( UUID gameId, UUID playerId ) throws Exception {
        URI uri = new URI(gameServiceURIString + "/games/" + gameId + "/players/" + playerId );
        mockServer.expect( ExpectedCount.once(), MockRestRequestMatchers.requestTo(uri) )
                .andExpect( MockRestRequestMatchers.method(HttpMethod.PUT) )
                .andRespond( MockRestResponseCreators.withSuccess() );
    }



}
