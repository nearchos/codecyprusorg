package org.codecyprus.th.api.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codecyprus.th.db.TreasureHuntFactory;
import org.codecyprus.th.model.Replies;
import org.codecyprus.th.model.TreasureHunt;
import org.codecyprus.th.model.Visibility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Logger;

public class TestListServlet extends HttpServlet {

    public static final String PARAMETER_NUMBER_OF_TH = "number-of-ths";
    public static final int DEFAULT_NUMBER_OF_TREASURE_HUNTS = 10;

    public static final Logger log = Logger.getLogger("codecyprus-test-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        // get parameters
        int numberOfTreasureHunts;
        try {
            final String numberOfTreasureHuntsS = request.getParameter(PARAMETER_NUMBER_OF_TH);
            numberOfTreasureHunts = Integer.parseInt(numberOfTreasureHuntsS);
        } catch (NumberFormatException nfe) {
            numberOfTreasureHunts = DEFAULT_NUMBER_OF_TREASURE_HUNTS;
        }
        if(numberOfTreasureHunts < 0) numberOfTreasureHunts = DEFAULT_NUMBER_OF_TREASURE_HUNTS;

        // formulate reply
        final Vector<TreasureHunt> selectedTreasureHunts = createRandomTreasureHunts(numberOfTreasureHunts);
        final Replies.ListReply listReply = new Replies.ListReply(selectedTreasureHunts);

        // parse to JSON and return results
        printWriter.println(gson.toJson(listReply));
    }

    private Vector<TreasureHunt> createRandomTreasureHunts(final int numberOfTreasureHunts) {
        final Vector<TreasureHunt> treasureHunts = new Vector<>();
        for(int i = 0; i < numberOfTreasureHunts; i++) {
            final TreasureHunt treasureHunt = new TreasureHunt(
                    UUID.randomUUID().toString(),
                    "name-" + i,
                    "description-" + i,
                    "email-" + i + "@example.com",
                    UUID.randomUUID().toString().substring(0, 8), // randomly generated secret code, limit to 8 chars
                    UUID.randomUUID().toString(), // randomly generated salt
                    getRandomVisibility(),
                    System.currentTimeMillis(),
                    System.currentTimeMillis() + 3600000L,
                    360000L * (1 + random.nextInt(10)),
                    flipCoin(),
                    flipCoin(),
                    flipCoin(),
                    flipCoin()
            );
            treasureHunts.add(treasureHunt);
        }
        return treasureHunts;
    }

    private static final Random random = new Random();
    private static final Visibility [] VISIBILITY_VALUES = Visibility.values();

    private Visibility getRandomVisibility() {
        return VISIBILITY_VALUES[random.nextInt(VISIBILITY_VALUES.length)];
    }

    private boolean flipCoin() {
        return random.nextBoolean();
    }
}