package com.todosapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.todosapp.data.CustomCursorAdapter;
import com.todosapp.data.MyTaskContentProvider;
import com.todosapp.data.TaskTable;

/*
 * TasksOverviewActivity displays the existing task items
 * in a list
 * 
 * You can create new ones via the ActionBar entry "Insert"
 * You can delete / edit existing ones via a long press on the item
 */

@TargetApi(19)
public class TasksOverviewActivity extends ListActivity implements
    LoaderManager.LoaderCallbacks<Cursor>, OnQueryTextListener {
  private static final int DELETE_ID = Menu.FIRST + 1;
  private static final int EDIT_ID = Menu.FIRST + 2;
  private static final int CANCEL_ID = Menu.FIRST + 3;

  private CustomCursorAdapter adapter;
  public static String sortBy = "";
  private SearchView mSearchView;
  private String mCurFilter;
  AlertDialog levelDialog;

  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.task_list);
    this.getListView().setDividerHeight(2);
    fillData();
    //http://developer.android.com/guide/topics/ui/actionbar.html#Dropdown
   
    registerForContextMenu(getListView());
  }

  // Create the menu based on the XML definition
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    new MenuInflater(this).inflate(R.menu.list_menu, menu);
 // Set up ShareActionProvider's default share intent
//    MenuItem shareItem = menu.findItem(R.id.sort);
//    Spinner spinnerNumber = (Spinner) shareItem.getActionView();
//
//    getActionBar().setDisplayShowTitleEnabled(false);
//    getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//    SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sortBySpinnerItems, android.R.layout.simple_spinner_dropdown_item);
//    getActionBar().setListNavigationCallbacks(mSpinnerAdapter , this);
//    
//    MenuItem item = menu.add("Search");
//    item.setIcon(android.R.drawable.ic_menu_search);
//    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
//            | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//    mSearchView = new MySearchView(this);
//    mSearchView.setOnQueryTextListener(this);
//    //mSearchView.setOnCloseListener( this);
//    mSearchView.setIconifiedByDefault(true);
//    item.setActionView(mSearchView);
    
    //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    //SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
    //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    return true;
  }
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, DELETE_ID, 0, R.string.context_menu_delete);
    menu.add(0, EDIT_ID, 0, R.string.context_menu_edit);
    menu.add(0, CANCEL_ID, 0, R.string.context_menu_cancel);

  }
//Reaction to the menu selection
 @Override
 public boolean onOptionsItemSelected(MenuItem item) {
   switch (item.getItemId()) {
   case R.id.insert:
     createTask();
     return true;
   case R.id.sort:
   	sortBy();
   	return true;
   case R.id.search:
	 search();
	 return true;
   }   
   return super.onOptionsItemSelected(item);
 }

@Override
 public boolean onContextItemSelected(MenuItem item) {
     AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
             .getMenuInfo();
     switch (item.getItemId()) {
     case DELETE_ID:
		Uri uri = Uri.parse(MyTaskContentProvider.CONTENT_URI + "/"
		      + info.id);
		
		getContentResolver().delete(uri, null, null);
		restartLoader();
		return true;
     case EDIT_ID:
       Intent intent = new Intent(this, TaskCreateEditActivity.class);
       Uri taskUri = Uri.parse(MyTaskContentProvider.CONTENT_URI + "/" + info.id);
       intent.putExtra(MyTaskContentProvider.CONTENT_ITEM_TYPE, taskUri);
       startActivity(intent);
       return true;
     case CANCEL_ID:
   	return true;
   }
   return super.onContextItemSelected(item);
 }
  public static class MySearchView extends SearchView {
      public MySearchView(Context context) {
          super(context);
      }

      // The normal SearchView doesn't clear its search text when
      // collapsed, so we will do this for it.
      @SuppressLint("NewApi")
	@Override
      public void onActionViewCollapsed() {
          setQuery("", false);
          super.onActionViewCollapsed();
      }
  }

  private void createTask() {
    Intent intent = new Intent(this, TaskCreateEditActivity.class);
    startActivity(intent);
  }

  private void sortBy() {  
	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
	      builder.setTitle("Sort tasks by")
	      		  .setSingleChoiceItems(R.array.sortBySpinnerItems, -1, new DialogInterface.OnClickListener() {
	      			  public void onClick(DialogInterface dialog, int item) {
	      				  switch(item) {
	      				  	case 0:
	      				  		sortBy= TaskTable.COLUMN_DESCRIPTION;
                              break;
	      				  	case 1:
	      				  		sortBy= TaskTable.COLUMN_DUEDATE;
                             break;
	      				  	case 2:
	      				  	//reverse sort order
	      				  	sortBy= TaskTable.COLUMN_PRIORITY + " DESC";
                             break;
	      			}
                  levelDialog.dismiss(); 
                  restartLoader();
                  }
              });
       levelDialog = builder.create();
       levelDialog.show();
  }
  private void search() {
	  String searchString = "Feed ";
	  Uri uri = Uri.parse(MyTaskContentProvider.CONTENT_TYPE);
	  String[] projection = { TaskTable.COLUMN_ID, TaskTable.COLUMN_DESCRIPTION, TaskTable.COLUMN_DUEDATE, TaskTable.COLUMN_PRIORITY, TaskTable.COLUMN_STATUS};
	  String selection = TaskTable.COLUMN_PRIORITY + " = '"
		        + ("2") + "'";
	  String[] selectionArgs = null;
	  String sortOrder = TaskTable.COLUMN_DESCRIPTION;
	  
	  if (TextUtils.isEmpty(searchString)) {
		    // Setting the selection clause to null will return all words
		    selection = null;
		    selectionArgs[0] = "";

		} else {
		    // Constructs a selection clause that matches the word that the user entered.
		    selection = TaskTable.COLUMN_PRIORITY + " IN  ( 2, 5 )";

		    // Moves the user's input string to the selection arguments.
		    //selectionArgs[0] = searchString;

		}
	  	  
	  getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);  
	  
	  //if(cursor==null) {
		  //Log.w("LISI", "null");
	  //} else if (cursor.getCount() < 1) {
		//  Log.w("LISI", "unsuccessful");
	  //} else {
		 //cursor.moveToFirst();
		  
		  //Log.w("LISI", String.valueOf(cursor.getCount()));
		  fillData();
	  //}

	  //cursor.close();
		
  }
  private void fillData() {
    getLoaderManager().initLoader(0, null, this);
    adapter = new CustomCursorAdapter(getApplication(), R.layout.task_row, null, 0);
    setListAdapter(adapter);
  }

  // Creates a new loader after the initLoader () call
  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    String[] projection = { TaskTable.COLUMN_ID, TaskTable.COLUMN_DESCRIPTION, TaskTable.COLUMN_DUEDATE, TaskTable.COLUMN_PRIORITY, TaskTable.COLUMN_STATUS};
    CursorLoader cursorLoader = new CursorLoader(this,
        MyTaskContentProvider.CONTENT_URI, projection, null, null, sortBy);
    return cursorLoader;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    adapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    // data is not available anymore, delete reference
    adapter.swapCursor(null);
  }
  	

@Override
public boolean onQueryTextChange(String arg0) {
	// Called when the action bar search text has changed.  Update
    // the search filter, and restart the loader to do a new query
    // with this filter.
    String newFilter = !TextUtils.isEmpty("") ? "" : null;
    // Don't do anything if the filter hasn't actually changed.
    // Prevents restarting the loader when restoring state.
    if (mCurFilter == null && newFilter == null) {
        return true;
    }
    if (mCurFilter != null && mCurFilter.equals(newFilter)) {
        return true;
    }
    mCurFilter = newFilter;
    getLoaderManager().restartLoader(0, null, this);
    return true;
}

@Override
public boolean onQueryTextSubmit(String arg0) {
	// TODO Auto-generated method stub
	return false;
}

/* Event listeners */

public void restartLoader() {
	getLoaderManager().restartLoader(0, null, this);
}


//@Override
//public void onClose(IOException e) {
//	if (!TextUtils.isEmpty(mSearchView.getQuery())) {
//        mSearchView.setQuery(null, true);
//    }
//    //return true;
//	
//	
//}

}

