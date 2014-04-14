package com.todosapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.LoaderManager;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;

import com.todosapp.data.CustomCursorAdapter;
import com.todosapp.data.MyTaskContentProvider;
import com.todosapp.data.TaskTable;

/*
 * TasksOverviewActivity displays the existing task items in a list
 * 
 * You can create new ones via the ActionBar entry "Insert"
 * You can delete / edit existing ones via a long press on the item
 */

@TargetApi(19)
public class TasksOverviewActivity extends ListActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {
  private static final int DELETE_ID = Menu.FIRST + 1;
  private static final int EDIT_ID = Menu.FIRST + 2;
  private static final int CANCEL_ID = Menu.FIRST + 3;

  private CustomCursorAdapter adapter;
  AlertDialog levelDialog;
  public static String sortBy = "";
  public static Cursor searchCursor;
  String searchByDescription = "";
  String searchByPriority = "";
  String searchByStatus = "";
  Uri uri = MyTaskContentProvider.CONTENT_URI;
  String[] projection = { TaskTable.COLUMN_ID, TaskTable.COLUMN_DESCRIPTION, TaskTable.COLUMN_DUEDATE, TaskTable.COLUMN_PRIORITY, TaskTable.COLUMN_STATUS};
  String  selection = "";
  
  // Moves the user's input string to the selection arguments
  String selectionArgs[] = new String[1];
  
  
 /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.task_list);
    this.getListView().setDividerHeight(2);
    initLoader();
    fillData(null);
    //http://developer.android.com/guide/topics/ui/actionbar.html#Dropdown
   
    registerForContextMenu(getListView());
  }

  // Create the menu based on the XML definition
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    new MenuInflater(this).inflate(R.menu.list_menu, menu);
    return true;
  }
  
  /** Select an item from the context menu */
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, DELETE_ID, 0, R.string.context_menu_delete);
    menu.add(0, EDIT_ID, 0, R.string.context_menu_edit);
    menu.add(0, CANCEL_ID, 0, R.string.context_menu_cancel);

  }

  /** Action bar related methods */
  // Insert
  private void createTask() {
    Intent intent = new Intent(this, TaskCreateEditActivity.class);
    startActivity(intent);
  }
  
  //Sort
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
                  if (searchCursor==null) { 
                	  restartLoader();
                  } else {
                	  //recreating the cursor in order to "pick up" the updated sorting order
                	  searchCursor=getContentResolver().query(uri, projection, selection, selectionArgs, sortBy);
                	  initLoader();
                	  fillData(searchCursor);
                	  
                  }
                  }
              });
       levelDialog = builder.create();
       levelDialog.show();
  	}
  	// Search...
  	private void search() {
	  //Create a search dialog
	  final Dialog dialog = new Dialog(this);
      dialog.setTitle("Search by the criteria...");

      //set dialog message
   			dialog.setContentView(R.layout.search_dialog);
   			dialog.setTitle("Search...");
   			final EditText search_description = (EditText) dialog.findViewById(R.id.todos_search_description);
			final Spinner priorityDropdown = (Spinner) dialog.findViewById(R.id.todos_search_priority);
			final Spinner statusDropdown = (Spinner) dialog.findViewById(R.id.todos_search_status);
			Button db = (Button) dialog.findViewById(R.id.todos_search_button);
			Button db1 = (Button) dialog.findViewById(R.id.todos_search_cancel_button);
			
				db.setOnClickListener(new OnClickListener() {
   					@Override
   					public void onClick(View v) {
   						//if (TextUtils.isEmpty(searchString)) {
   							//Setting the selection clause to null will return all words
   							//searchString = "";
   							//} else {
   							    
   								// Constructs a selection clause that matches the word that the user entered.
   								searchByDescription = search_description.getText().toString();
   								searchByPriority = priorityDropdown.getSelectedItem().toString();
   								searchByStatus = statusDropdown.getSelectedItem().toString();
   					   			
   							//}
   								boolean priorityFlag = searchByPriority.equals(priorityDropdown.getItemAtPosition(0));
   								
   								selectionArgs[0] = "%" + searchByDescription + "%";
   								
   								//assuming priority is always equal to 1...
   								String queriedPriority = (priorityFlag==true) ? "1,2,3,4,5" : searchByPriority;
   								//selectionArgs[2] =  searchByStatus;
   								Log.w("LISI", queriedPriority);
   								String selectionClause1 = " AND " + TaskTable.COLUMN_PRIORITY + " IN (" + queriedPriority + ")";
   								//String selectionClause2 = searchByStatus.equals(statusDropdown.getItemAtPosition(0)) ? TaskTable.COLUMN_STATUS : " AND " + TaskTable.COLUMN_STATUS + "=?";
   								
   								selection = TaskTable.COLUMN_DESCRIPTION + " LIKE? " 
   										  + selectionClause1;
   								
   						  			 // + selectionClause2;
   								searchCursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortBy);
   								
   								
   		   						fillData(searchCursor);
   								dialog.dismiss();
   							}
   						});
   						db1.setOnClickListener(new OnClickListener() {
   							@Override
   							public void onClick(View v) {
   								
   								dialog.dismiss();
   							}
   						});
   			 
   						dialog.show();
   				 
	  if(searchCursor==null) {
		 
	  } else if (searchCursor.getCount() < 1) {
		  
	  } else {
		 // searchCursor.moveToFirst();
		  //Log.w("LISI", String.valueOf(searchCursor.getCount()));
		  
	  } 
		
  }
  //View all
  private void view_all() {
  	restartLoader();	
  }
  
  //Fill data
  private void fillData(Cursor cursor) {
	 adapter = new CustomCursorAdapter(getApplication(), R.layout.task_row, cursor, 0);
	 setListAdapter(adapter);
  }

  /** Loader related methods */
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
  private void initLoader() {
	  getLoaderManager().initLoader(0, null, this);
  }
  public void restartLoader() {
		getLoaderManager().restartLoader(0, null, this);
	}

  /* Event listeners */
  /** Select an item from the action bar menu */
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
    case R.id.all:
 		 view_all();
 		 return true;
    }   
    return super.onOptionsItemSelected(item);
  }
  /** Select an item from the context menu menu */
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

}

