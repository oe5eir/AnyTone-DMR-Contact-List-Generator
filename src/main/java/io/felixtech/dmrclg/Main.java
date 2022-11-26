package io.felixtech.dmrclg;

/*
 *  Copyright (C) 2022 OE5EIR @ https://www.oe5eir.at/
 *
 *  Licensed under the GNU General Public License v3.0 (the "License").
 *  You may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.apache.commons.text.CaseUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Generator for Contact Lists for AnyTone DMR Radios
 * API Doc: https://database.radioid.net/database/api
 * Parameter: Output File
 */
public final class Main {
    private static final String SEARCH_STRING = "country=Austria&country=Germany";
    private static final String[] SKIP_CALLSIGN_STARTS_WITH = { "DA", "DB0", "DF0", "DK0", "DL0", "DM0", "DN0", "DO0", "DP", "DQ", "DR", "DE" };

    public static void main(String[] args) throws IOException {
        System.out.println("Preparing...");

        URL url = new URL("https://database.radioid.net/api/dmr/user/?" + SEARCH_STRING);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.setInstanceFollowRedirects(false);

        System.out.println("Querying...");

        int status = con.getResponseCode();

        System.out.println("Status: " + status);
        System.out.println("Building list...");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        con.disconnect();

        JSONObject response = new JSONObject(content.toString());
        JSONArray list = response.getJSONArray("results");

        File csvFile = new File(args[0]);

        System.out.println("Writing file...");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            int i = 1;
            writer.write("\"No.\",\"Radio ID\",\"Callsign\",\"Name\",\"City\",\"State\",\"Country\",\"Remarks\",\"Call Type\",\"Call Alert\"\r\n");

            listLoop:
            for (Object raw : list) {
                JSONObject entry = (JSONObject) raw;

                // Skip: Austria OE?X* (Repeaters)
                if (entry.getString("callsign").startsWith("OE") && entry.getString("callsign").charAt(3) == 'X')
                    continue;

                for (String s : SKIP_CALLSIGN_STARTS_WITH) {
                    if (entry.getString("callsign").startsWith(s))
                        continue listLoop;
                }

                String city = entry.getString("city");
                city = city.replaceAll("\\d","");
                city = city.replace("A-","");
                city = city.replace(" A ","");
                city = city.replace("ß","ss");
                city = city.replace("ä","ae");
                city = city.replace("ö","oe");
                city = city.replace("ü","ue");
                city = city.replace("Vienna","Wien");
                city = CaseUtils.toCamelCase(city, true, ' ', '-', '.');
                city = city.trim();

                writer.write(String.format("\"%d\",\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"\",\"Private Call\",\"None\"\r\n", i++,
                        entry.getInt("id"),
                        entry.getString("callsign"),
                        entry.getString("fname"),
                        city,
                        entry.getString("state"),
                        entry.getString("country")
                ));
            }
        }

        System.out.println("Done!");
        System.out.println("List Location: " + csvFile.getAbsoluteFile());
    }
}
