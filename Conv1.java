import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alex73.korpus.voice.Fanetyka3;
import org.apache.commons.io.IOUtils;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/fannochange")
public class Conv1 extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String text;
            try (BufferedReader reader = req.getReader()) {
                text = IOUtils.toString(reader);
            }

            StringBuilder out = new StringBuilder();
            for (String w : text.split("\\s")) {
                w = Fanetyka3.fanetykaSlova(w);
                out.append(w).append(' ');
            }
            resp.setContentType("text/plain; charset=UTF-8");
            resp.getOutputStream().write(out.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            resp.setContentType("text/plain; charset=UTF-8");
            resp.getOutputStream().write(("Памылка: " + ex.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }
}
