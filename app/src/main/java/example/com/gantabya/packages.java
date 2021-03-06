package example.com.gantabya;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Prasis on 11/10/2015.
 */
public class packages extends Fragment implements AdapterView.OnItemClickListener {

    ListView listView;
    ProgressBar loader;
    static ArrayList<HashMap<String, String>> information=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview;
        packageslist obj = new packageslist();
        obj.execute();
        myview = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ListView) myview.findViewById(R.id.listView);
        loader = (ProgressBar) myview.findViewById(R.id.loader);
        listView.setOnItemClickListener(this);
        return myview;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment packagedetail = new package_detail(information.get(position));
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, packagedetail);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // funciton to get and manage the xml content
    public class packageslist extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
            String downloadURL = "http://gantabya.cu.cc/xml/packages/"; //url to download the xml content from
            ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
            try {
                URL url = new URL(downloadURL);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                results = processXML(inputStream);


            } catch (Exception e) {
                results = null;
                Log.d("gantabya", "error at doinbackground");
            }

        return results;
    }

        @Override
        protected void onCancelled() {
            Log.d("gantabya", "cancelled");
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> results) {
            try {
                packageadapter adapter = new packageadapter(getContext(), results);
                listView.setAdapter(adapter);
                information = new ArrayList<HashMap<String, String>>();
                information = results;
                loader.setVisibility(View.GONE); // Progress bar disappears once the listview is ready to be displayed.
            }
            catch (Exception e)
            {
                Log.d("gantabya","error at onpostexecute");
            }
        }

        public ArrayList<HashMap<String,String>> processXML(InputStream inputstream) throws Exception {

            //ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();

            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document xmlDocument = documentBuilder.parse(inputstream);

                Element rootelement = xmlDocument.getDocumentElement();

                NodeList itemslist = rootelement.getElementsByTagName("Package"); // in our app item = packages

                Node currentitem = null;
                NodeList currentiem_childrenlist = null;
                HashMap<String, String> currentmap = null;
                ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
                Node currentchild = null;
                String packagetype =null;
                int count = 0;
                for (int i = 0; i <itemslist.getLength(); i++) {
                    currentitem = itemslist.item(i);
                    currentmap = new HashMap<String, String>(); // current map is for package attributes
                    // for attribute of root node
                    currentmap.put("packagename",currentitem.getAttributes().getNamedItem("Name").getTextContent());
                    currentmap.put("packageplace",currentitem.getAttributes().getNamedItem("Place").getTextContent());
                    currentmap.put("packageduration",currentitem.getAttributes().getNamedItem("Duration").getTextContent());
                    currentmap.put("packageprice",currentitem.getAttributes().getNamedItem("Cost").getTextContent());
                    currentmap.put("packageimage",currentitem.getAttributes().getNamedItem("Image").getTextContent());
                    currentmap.put("packagebesttime",currentitem.getAttributes().getNamedItem("Season").getTextContent());
                    currentmap.put("packageduration",currentitem.getAttributes().getNamedItem("Duration").getTextContent());
                    currentiem_childrenlist = currentitem.getChildNodes();

                    //for further child nodes
                    for(int j=0;j<currentiem_childrenlist.getLength();j++)
                    {
                        currentchild = currentiem_childrenlist.item(j);
                        if(currentchild.getNodeName().equalsIgnoreCase("Itinerary"))
                        {
                            currentmap.put("packageitinerary",currentchild.getTextContent());
                        }
                        if(currentchild.getNodeName().equalsIgnoreCase("CostInclusion"))
                        {
                            currentmap.put("packagecostinclusion",currentchild.getTextContent());
                        }
                        if(currentchild.getNodeName().equalsIgnoreCase("CostExclusion"))
                        {
                            currentmap.put("packagecostexclusion",currentchild.getTextContent());
                        }
                        if(currentchild.getNodeName().equalsIgnoreCase("Overview"))
                        {
                            currentmap.put("packageoverview",currentchild.getTextContent());
                        }

                        if(currentchild.getNodeName().equalsIgnoreCase("Detail"))
                        {
                            currentmap.put("packagedetail",currentchild.getTextContent());
                        }
                        if(currentchild.getNodeName().equalsIgnoreCase("Company"))
                        {
                            currentmap.put("packagecompanyname",currentchild.getAttributes().getNamedItem("Name").getTextContent());
                            currentmap.put("packagecompanyphone",currentchild.getAttributes().getNamedItem("Phone").getTextContent());
                        }

                        if(currentchild.getNodeName().equalsIgnoreCase("PackageTypes"))
                        {
                          for(int k=0;k<currentchild.getChildNodes().getLength();k++) {
                              if(currentchild.getChildNodes().item(k).getNodeName().equalsIgnoreCase("Type")){
                              packagetype = currentchild.getChildNodes().item(k).getAttributes().getNamedItem("Name").getTextContent();
                              currentmap.put("packagetype", packagetype);}
                          }
                        }

                    }


                    if (currentmap != null && !currentmap.isEmpty())
                        results.add(currentmap);
                }
                return results;

            } catch (Exception e) {
                Log.d("gantabya", "error at processxml");
                return null;

            }
        }

    }
}
class packageadapter extends BaseAdapter
{
    ArrayList<HashMap<String,String>> dataSource;
    Context context;
    LayoutInflater layoutInflater;
    Myholder holder = null;
    public packageadapter(Context context, ArrayList<HashMap<String, String>> dataSource)
    {
        this.dataSource = dataSource;
        this.context = context;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Myholder holder = null;
        if(row==null)
        {
            row = layoutInflater.inflate(R.layout.packages,parent,false);
            holder = new Myholder(row);
            row.setTag(holder);
        }
        else
        {
            holder = (Myholder) row.getTag();
        }
        HashMap<String,String> currentItem = dataSource.get(position);
        holder.title.setText(currentItem.get("packagename"));
        holder.pubdate.setText("Rs."+currentItem.get("packageprice"));
        holder.duration.setText(currentItem.get("packageduration")+" day(s)");
        Picasso.with(context).load(currentItem.get("packageimage")).into(holder.image);
            return row;
    }
}
class Myholder{
    TextView title, pubdate, duration;
    RatingBar rate;
    ImageView image;
    public Myholder(View view)
    {
        title = (TextView) view.findViewById(R.id.title);
        pubdate= (TextView) view.findViewById(R.id.pubdate);
        duration=(TextView)view.findViewById(R.id.duration);
        image = (ImageView) view.findViewById(R.id.packageimage);

    }
}


