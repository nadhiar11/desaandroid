package com.example.pattimura.sundawenang.Fragment;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import com.example.pattimura.sundawenang.Adapter.AdpterLowongan;
import com.example.pattimura.sundawenang.Model.AspirasiModel;
import com.example.pattimura.sundawenang.Model.LowonganModel;
import com.example.pattimura.sundawenang.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Lowongan extends Fragment {
    AdpterLowongan adapter;
    ArrayList<LowonganModel> daftarlowongan = new ArrayList<>();
    RelativeLayout kosong;
    ListView listlowongan;
    int currentpage, lastpage, banyakdata, currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
    private ProgressDialog mProgressDialog;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences.Editor editor;
    private String TAG;

    public Lowongan() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lowongan, container, false);

        currentpage = 1;
        lastpage = 1;


        listlowongan = (ListView) v.findViewById(R.id.listLowongan);
        kosong = (RelativeLayout) v.findViewById(R.id.layoutlowongan);
        adapter = new AdpterLowongan(Lowongan.this.getContext(), daftarlowongan);
        listlowongan.setAdapter(adapter);
        showProgressDialog();
        getAlllowongan(currentpage);
        adapter.notifyDataSetChanged();

        final PullRefreshLayout layout = (PullRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (currentpage != lastpage) {
                            daftarlowongan = new ArrayList<>();
                            adapter = new AdpterLowongan(Lowongan.this.getContext(), daftarlowongan);
                            getAlllowongan(currentpage);
                        }
                        layout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        listlowongan.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentScrollState = scrollState;
                isScrollCompleted();
            }

            private void isScrollCompleted() {
                if (currentVisibleItemCount > 0 && currentScrollState == SCROLL_STATE_IDLE) {
                    /*** In this way I detect if there's been a scroll which has completed ***/
                    /*** do the work for load more date! ***/
                    if (currentpage != lastpage) {
                        currentpage++;
                        getAlllowongan(currentpage);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Toast.makeText(Aspirasi.this.getContext(), Integer.toString(totalItemCount), Toast.LENGTH_SHORT).show();
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItemCount = visibleItemCount;
            }
        });


        return v;
    }

//    private void getAlllowongan() {
//        daftarlowongan.add(new LowonganModel("Membangun rumah", "17 Februari 2017", lorem()));
//        daftarlowongan.add(new LowonganModel("Merangkai corak bunga", "16 Februari 2017", lorem()));
//        daftarlowongan.get(0).addGambar("Membangun Rumah", R.drawable.oranggenteng);
//        daftarlowongan.get(1).addGambar("Merangkai Corak Bunga", R.drawable.ibujahit);
//    }
//
//    private String lorem() {
//        return "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
//    }

    void getAlllowongan(int page) {
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://212.237.31.161/api/job?page=" + page,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            daftarlowongan = new ArrayList<>();
                            JSONObject listdata = new JSONObject(response);
                            currentpage = listdata.getInt("current_page");
                            lastpage = listdata.getInt("last_page");
                            banyakdata = listdata.getInt("total");
                            JSONArray isiproduk = listdata.getJSONArray("data");
                            for (int i = 0; i < isiproduk.length(); i++) {
                                JSONObject object = isiproduk.getJSONObject(i);
                                LowonganModel asp = new LowonganModel(object.getString("job_name"), object.getString("created_at"), object.getString("description"), object.getString("job_owner"), object.getString("phone"));
//                                //Toast.makeText(Lowongan.this.getContext(), asp.getIsi(), Toast.LENGTH_SHORT).show();
                                asp.addGambar("Lowongan", "http://212.237.31.161/storage/" + object.getString("photo"));
                                daftarlowongan.add(asp);
                            }
//                            for (int i = 0; i < listdata.length(); i++) {
//                                JSONObject object = listdata.getJSONObject(i);
//                                LowonganModel asp = new LowonganModel(object.getString("job_name"), object.getString("created_at"), object.getString("description"), object.getString("job_owner"), object.getString("phone"));
//                                //Toast.makeText(Lowongan.this.getContext(), asp.getIsi(), Toast.LENGTH_SHORT).show();
//                                daftarlowongan.add(asp);
//                            }
                            if (!daftarlowongan.isEmpty()) {
                                hideProgressDialog();
                                kosong.setVisibility(View.GONE);
                                adapter = new AdpterLowongan(Lowongan.this.getContext(), daftarlowongan);
                                listlowongan.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                listlowongan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        LowonganModel pm = daftarlowongan.get(position);
                                        Bundle b = new Bundle();
                                        Fragment f = new DetailLowongan();
                                        b.putParcelable("Lowongan", pm);
                                        f.setArguments(b);
                                        TAG = "Lowongan";
                                        editor.putString("TAG", TAG);
                                        editor.commit();
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.replace(R.id.mainframe, f);
                                        ft.addToBackStack(TAG);
                                        ft.commit();
                                    }
                                });
                            } else {
                            }
                        } catch (Exception e) {
                            Toast.makeText(Lowongan.this.getContext(), "error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
//tempat response di dapatkan
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        error.printStackTrace();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(Lowongan.this.getContext(), "Tidak dapat memuat data\nTolong perika koneksi internet anda !", Toast.LENGTH_LONG).show();
                            hideProgressDialog();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                try {
                    //Adding parameters to request

                    //returning parameter
                    return params;
                } catch (Exception e) {
                    Toast.makeText(Lowongan.this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return params;
                }
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(stringRequest);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Lowongan.this.getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(Lowongan.this.getContext());
            mProgressDialog.setMessage("Mohon tunggu, sedang mengambil data !");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
