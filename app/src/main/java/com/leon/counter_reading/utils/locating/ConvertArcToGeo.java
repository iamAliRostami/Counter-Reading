package com.leon.counter_reading.utils.locating;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class ConvertArcToGeo {
    public static CustomGeoJSON convertPolygon(CustomArcGISJSON customArcGISJSON, String type) {
        CustomGeoJSON customGeoJSON = new CustomGeoJSON();
        customGeoJSON.type = "FeatureCollection";
        customGeoJSON.features = new ArrayList<>();
        if (customArcGISJSON.features != null) {
            for (int i = 0; i < customArcGISJSON.features.size(); i++) {
                FeatureArc featureArc = customArcGISJSON.features.get(i);
                FeatureGIS featureGIS = new FeatureGIS();
                featureGIS.type = "Feature";
                featureGIS.id = i;
                featureGIS.properties.OBJECTID = featureArc.attributes.OBJECTID;
                featureGIS.properties.CODE = featureArc.attributes.CODE;
                featureGIS.properties.e_ID = featureArc.attributes.e_ID;
                featureGIS.properties.Serial = featureArc.attributes.Serial;

                //waterPipe
                featureGIS.properties.P_Name_Shahr = featureArc.attributes.P_Name_Shahr;
                featureGIS.properties.p_Map_Number = featureArc.attributes.p_Map_Number;
                featureGIS.properties.p_Shomare_Porofil = featureArc.attributes.p_Shomare_Porofil;
                featureGIS.properties.p_Address = featureArc.attributes.p_Address;
                featureGIS.properties.p_Noe_Lule = featureArc.attributes.p_Noe_Lule;
                featureGIS.properties.p_Ghotr_Lule = featureArc.attributes.p_Ghotr_Lule;
                featureGIS.properties.p_Jens_Lule = featureArc.attributes.p_Jens_Lule;
                featureGIS.properties.p_Omgh_Nasb = featureArc.attributes.p_Omgh_Nasb;
                featureGIS.properties.p_Poshesh_Zamin_Roylole = featureArc.attributes.p_Poshesh_Zamin_Roylole;
                featureGIS.properties.p_Poshesh_Sathe_Zamin = featureArc.attributes.p_Poshesh_Sathe_Zamin;
                featureGIS.properties.p_Hefazat_Dakheli = featureArc.attributes.p_Hefazat_Dakheli;
                featureGIS.properties.p_Flow_Average = featureArc.attributes.p_Flow_Average;
                featureGIS.properties.p_Nahve_Enteghal = featureArc.attributes.p_Nahve_Enteghal;
                featureGIS.properties.p_Pressure_Max = featureArc.attributes.p_Pressure_Max;
                featureGIS.properties.p_Pressure_Minpublic = featureArc.attributes.p_Pressure_Minpublic;
                featureGIS.properties.p_Mizane_Max_Zarbeh_Ghochpublic = featureArc.attributes.p_Mizane_Max_Zarbeh_Ghochpublic;
                featureGIS.properties.p_Tarikhe_Nasbpublic = featureArc.attributes.p_Tarikhe_Nasbpublic;
                featureGIS.properties.p_Karkhane_Sazandehpublic = featureArc.attributes.p_Karkhane_Sazandehpublic;
                featureGIS.properties.p_Peymankarpublic = featureArc.attributes.p_Peymankarpublic;
                featureGIS.properties.p_Akspublic = featureArc.attributes.p_Akspublic;
                featureGIS.properties.p_Tozihatpublic = featureArc.attributes.p_Tozihatpublic;
                //waterPipe
                //WaterTransfer
                featureGIS.properties.lf_Name_Mantagheh = featureArc.attributes.lf_Name_Mantagheh;
                featureGIS.properties.lf_Name_Shahr = featureArc.attributes.lf_Name_Shahr;
                featureGIS.properties.lf_Name_TakmilKonandeh = featureArc.attributes.lf_Name_TakmilKonandeh;
                featureGIS.properties.lf_Tarikh_Takmil = featureArc.attributes.lf_Tarikh_Takmil;
                featureGIS.properties.lf_Saat_Takmil = featureArc.attributes.lf_Saat_Takmil;
                featureGIS.properties.lf_Adrees = featureArc.attributes.lf_Adrees;
                featureGIS.properties.lf_Code_Manhol_Baladast = featureArc.attributes.lf_Code_Manhol_Baladast;
                featureGIS.properties.lf_Code_Manhol_Paindast = featureArc.attributes.lf_Code_Manhol_Paindast;
                featureGIS.properties.lf_Tarikh_Ehdas = featureArc.attributes.lf_Tarikh_Ehdas;
                featureGIS.properties.lf_Tarikh_Bahrebardari = featureArc.attributes.lf_Tarikh_Bahrebardari;
                featureGIS.properties.lf_Omgh_Ebteda = featureArc.attributes.lf_Omgh_Ebteda;
                featureGIS.properties.lf_Omgh_Enteha = featureArc.attributes.lf_Omgh_Enteha;
                featureGIS.properties.lf_Motovaset_omgh_Loleh = featureArc.attributes.lf_Motovaset_omgh_Loleh;
                featureGIS.properties.lf_Jens_Lule = featureArc.attributes.lf_Jens_Lule;
                featureGIS.properties.lf_Mahale_Gharar_Fazelabro = featureArc.attributes.lf_Mahale_Gharar_Fazelabro;
                featureGIS.properties.lf_Tole_Loleh = featureArc.attributes.lf_Tole_Loleh;
                featureGIS.properties.lf_Shib = featureArc.attributes.lf_Shib;
                featureGIS.properties.lf_Shedate_BareTerafik = featureArc.attributes.lf_Shedate_BareTerafik;
                featureGIS.properties.lf_Akharin_Shostesho = featureArc.attributes.lf_Akharin_Shostesho;
                featureGIS.properties.lf_Mizane_Rosob = featureArc.attributes.lf_Mizane_Rosob;
                featureGIS.properties.lf_Noe_Rosob = featureArc.attributes.lf_Noe_Rosob;
                featureGIS.properties.lf_Tozihat = featureArc.attributes.lf_Tozihat;
                //WaterTransfer
                featureGIS.properties.State = featureArc.attributes.State;
                featureGIS.properties.X = featureArc.attributes.X;
                featureGIS.properties.Y = featureArc.attributes.Y;
                featureGIS.properties.Z = featureArc.attributes.Z;
                featureGIS.properties.pa_Name_Mantaghe = featureArc.attributes.pa_Name_Mantaghe;
                featureGIS.properties.pa_Name_Shahr = featureArc.attributes.pa_Name_Shahr;
                featureGIS.properties.pa_Noe_Karbari = featureArc.attributes.pa_Noe_Karbari;
                featureGIS.properties.Shape_Area_1 = featureArc.attributes.Shape_Area_1;
                featureGIS.geometry = new GeometryGIS();
                featureGIS.geometry.type = type;
                if (featureArc.geometry.rings != null)
                    featureGIS.geometry.coordinates = featureArc.geometry.rings;
                else
                    featureGIS.geometry.coordinates = featureArc.geometry.paths;
                customGeoJSON.features.add(featureGIS);
            }
        }
        return customGeoJSON;
    }

    public static CustomArcGISJSON convertStringToCustomArcGISJSON(String s) {
        s = s.replace("window._EsriLeafletCallbacks.c5(", "");
        s = s.substring(0, s.length() - 2);
        Gson gsonArc = new GsonBuilder().setLenient().create();
        return gsonArc.fromJson(s, CustomArcGISJSON.class);
    }

    public static String convertCustomGeoJSONToString(CustomGeoJSON customGeoJSON) {
        Gson gsonGeo = new Gson();
        return gsonGeo.toJson(customGeoJSON);
    }
}
