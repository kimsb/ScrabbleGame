package scrabblegamepkg.server;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PlayerTest {

    private Rack rack = mock(Rack.class);

    @Test
    public void new_player_has_zero_score() {
        Player test = new Player("Test", rack);

        assertThat(test.getScore()).isEqualTo(0);
    }

    @Test
    public void add_returns_the_new_score() {
        Player test = new Player("Test", rack);

        assertThat(test.addScore(23)).isEqualTo(23);
    }

    @Test
    public void add_score_adds_score_permanently() {
        Player test = new Player("Test", rack);

        test.addScore(13);

        assertThat(test.getScore()).isEqualTo(13);
    }

}