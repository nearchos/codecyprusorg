package org.codecyprus.th.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.codecyprus.th.db.TreasureHuntFactory;
import org.codecyprus.th.model.TreasureHunt;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.logging.Logger;

public class ListServlet extends HttpServlet {

    public static final String PARAMETER_INCLUDE_FINISHED = "include-finished";

    public static final Logger log = Logger.getLogger("codecyprus-th");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Handles request to list available {@link org.codecyprus.th.model.TreasureHunt}.
     * URL form: /api/list[?include-finished | ?detailed-view | ?include-finished&detailed-view]
     *
     * Example:
     * <ul>
     *     <li><b>/api/list</b> shows a list of all {@link org.codecyprus.th.model.TreasureHunt}s, which are either
     *     active or start in the future.</li>
     *     <li><b>/api/list?include-finished</b> shows a list of all {@link org.codecyprus.th.model.TreasureHunt}s,
     *     including those which are already finished.</li>
     * </ul>
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        final PrintWriter printWriter = response.getWriter();

        // get parameters
        final boolean includeFinished = request.getParameter(PARAMETER_INCLUDE_FINISHED) != null;

        // formulate reply
        final Vector<TreasureHunt> selectedTreasureHunts = TreasureHuntFactory.getPublicTreasureHunts(includeFinished);
        final Reply reply = new Reply(selectedTreasureHunts);

        // parse to JSON and return results
        printWriter.println(gson.toJson(reply));
    }

    public class Reply {
        private String status = "OK";
        @SerializedName("treasureHunts")
        private final Vector<TreasureHunt> selectedTreasureHunts;

        public Reply(Vector<TreasureHunt> selectedTreasureHunts) {
            this.selectedTreasureHunts = selectedTreasureHunts;
        }

        public String getStatus() {
            return status;
        }

        public Vector<TreasureHunt> getSelectedTreasureHunts() {
            return selectedTreasureHunts;
        }
    }
}