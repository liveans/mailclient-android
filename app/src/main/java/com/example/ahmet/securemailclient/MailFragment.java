package com.example.ahmet.securemailclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ahmet.securemailclient.dummy.DummyContent;
import com.example.ahmet.securemailclient.dummy.DummyContent.DummyItem;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;

public class MailFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyMailRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    public static MailFragment instance=null;
    private boolean state=false;
    private boolean stateActive=true;

    public void refreshData() {
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public static MailFragment getInstance() {
        return instance;
    }

    public MailFragment() {
        if (instance==null) {
            instance=this;
        }
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MailFragment newInstance(int columnCount) {
        MailFragment fragment = new MailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail_list, container, false);

        // Set the adapter
        //if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerViewAdapter=new MyMailRecyclerViewAdapter(DummyContent.ITEMS, mListener);

            recyclerView.setAdapter(recyclerViewAdapter);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(dividerItemDecoration);
            final SwipeRefreshLayout swipeRefreshLayout=view.findViewById(R.id.swiperefresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    final ProgressDialog dialog=ProgressDialog.show(getActivity(), "Refreshing",
                            "We are receiving your emails to refresh your list. Please wait...", true);
                    ExecutorService service=Executors.newSingleThreadExecutor();
                    service.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MailClient.getInstance().receive(DummyContent.ITEMS.size());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MailFragment.getInstance().refreshData();
                                        swipeRefreshLayout.setRefreshing(false);
                                        if (dialog!=null) {
                                            dialog.setIndeterminate(false);
                                            dialog.dismiss();
                                        }
                                    }
                                });
                                //swipeRefreshLayout.setRefreshing(false);
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    service.shutdown();
                    System.out.println("happening.");
                }
            });
            recyclerView.setVerticalScrollBarEnabled(true);
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            /*final EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, final int totalItemsCount, RecyclerView view) {
                    if (state || !state) return;

                    final ProgressDialog dialog=ProgressDialog.show(getActivity(), "Loading",
                            "We are receiving your emails to load more on your list. Please wait...", true);
                    ExecutorService service=Executors.newSingleThreadExecutor();
                    service.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                state=false;
                                stateActive=false;
                                MailClient.getInstance().receiveMore(totalItemsCount);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MailFragment.getInstance().refreshData();
                                        if (dialog!=null) {
                                            dialog.setIndeterminate(false);
                                            dialog.dismiss();
                                        }
                                        recyclerView.scrollToPosition(totalItemsCount+1);
                                        state=true;
                                    }
                                });
                                //swipeRefreshLayout.setRefreshing(false);
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    service.shutdown();
                }
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView,dx,dy);
                    int topRowVerticalPosition =
                            (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                    swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
                    if (state && !stateActive) {
                        stateActive=true;
                        this.resetState();
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
            };*/
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView,dx,dy);
                    int topRowVerticalPosition =
                            (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                    swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        //}
        state=true;
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
