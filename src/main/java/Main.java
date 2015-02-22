import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

           import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    if (req.getRequestURI().endsWith("/db")) {
      showDatabase(req,resp);
    } else {
      showHome(req,resp);
    }
  }

  private void showHome(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.getWriter().print("Hello from Java!");
  }

  private void showDatabase(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
       Connection connection = null;
    try {
connection = getConnection();

      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      String out = "Hello!\n";
      while (rs.next()) {
          out += "Read from DB: " + rs.getTimestamp("tick") + "\n";
      }

      resp.getWriter().print(out);
    } catch (Exception e) {
      resp.getWriter().print("There was an error: " + e.getMessage());
    } finally {
      if (connection != null) try{connection.close();} catch(SQLException e){}
    }
  }

  private Connection getConnection() throws URISyntaxException, SQLException {
    URI dbUri = new URI(System.getenv("DATABASE_URL"));

    String username = dbUri.getUserInfo().split(":")[0];
  //    String password = dbUri.getUserInfo().split(":")[1];
    int port = dbUri.getPort();

    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + port + dbUri.getPath();

    return DriverManager.getConnection(dbUrl, username, password);
  }

  // service() responds to both GET and POST requests.
  // You can also use doGet() or doPost()
  public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String msg = request.getParameter("Body");
      msg = msg.toLowerCase();
      System.out.println("-->" + msg);

      String result = "";

      if (msg.equals("usage")) {
        System.out.println("usage");
        result = "Twilio Movie Assistant Usage Guide:"
          + "\nlist: Returns a list of movies available"
          + "\nmovie name: Returns the movie's showtimes";
      } else if (msg.equals("list")) {
        System.out.println("list");
Document doc;
        try {
          doc = Jsoup.connect("http://www.imdb.com/showtimes/cinema/CA/ci0961718/CA/H2W1G6").get();
          Elements titles = doc.select(".info > h3 > span > a");
          System.out.println(titles.size());
          for (int i = 0; i < titles.size(); i++) {
            String title = titles.get(i).text().split("\\(")[0];
            result += "\n" + i + "-" + title;
          }
        } catch (IOException e) {
               e.printStackTrace();
        }
      } else {
        System.out.println("showtimes");
        Document doc;
        try {
          doc = Jsoup.connect("http://www.imdb.com/showtimes/cinema/CA/ci0961718/CA/H2W1G6").get();
              Elements titles = doc.select(".info > h3 > span > a");
          Elements showtimes = doc.getElementsByClass("showtimes");

          System.out.println(showtimes.size() + " , " + titles.size());
          for (int i = 0; i < titles.size(); i++) {
            String title = titles.get(i).text().toLowerCase().split("\\(")[0];
  //             title = title.substring(0, title.length()-1);
            if (title.equals(msg) || (""+i).equals(msg)) {
              result = (titles.get(i).text() + " : " + showtimes.get(i).text());
            }
          }
          
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      
      if (result.equals("")) {
        result = "Cannot complete command: " + msg;
      }

      Message message = new Message(result);
           TwiMLResponse twiml = new TwiMLResponse();
      try {
          twiml.append(message);
			} catch (TwiMLException e) {
          e.printStackTrace();
      }

      response.setContentType("application/xml");
      response.getWriter().print(twiml.toXML());
  }

  public static void main(String[] args) throws Exception {
    Server server = new Server(Integer.valueOf(System.getenv("PORT")));
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);
    context.addServlet(new ServletHolder(new Main()),"/*");
    server.start();
    server.join();
  }
}