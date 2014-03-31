package com.todosapp;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

//import com.todosapp.TodosOverview.MyAdapter;
import com.todosapp.R;
import com.todosapp.data.MyTaskContentProvider;
import com.todosapp.data.TaskTable;

/*
 * TasksOverviewActivity displays the existing task items
 * in a list
 * 
 * You can create new ones via the ActionBar entry "Insert"
 * You can delete / edit existing ones via a long press on the item
 */

public class TasksOverviewActivity extends ListActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {
  private static final int DELETE_ID = Menu.FIRST + 1;
  private static final int EDIT_ID = Menu.FIRST + 2;
  private static final int CANCEL_ID = Menu.FIRST + 3;

  private CustomCursorAdapter adapter;
  private ShareActionProvider mShareActionProvider;

  
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
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.list_menu, menu);
 // Set up ShareActionProvider's default share intent
    //MenuItem shareItem = menu.findItem(R.id.sort);
    //mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
    //mShareActionProvider.setShareIntent(getDefaultIntent());
    return true;
  }

  // Reaction to the menu selection
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.insert:
      createTask();
      return true;
    case R.id.sort:
    	sortBy();
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
		fillData();
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

  private void createTask() {
    Intent intent = new Intent(this, TaskCreateEditActivity.class);
    startActivity(intent);
  }
  private void sortBy() {
	  
  }

  private void fillData() {

    getLoaderManager().initLoader(0, null, this);

    adapter = new CustomCursorAdapter(getApplication(), R.layout.task_row, null, 0);

    setListAdapter(adapter);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, DELETE_ID, 0, R.string.context_menu_delete);
    menu.add(0, EDIT_ID, 0, R.string.context_menu_edit);
    menu.add(0, CANCEL_ID, 0, R.string.context_menu_cancel);
  }

  // Creates a new loader after the initLoader () call
  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    String[] projection = { TaskTable.COLUMN_ID, TaskTable.COLUMN_DESCRIPTION, TaskTable.COLUMN_DUEDATE, TaskTable.COLUMN_PRIORITY, TaskTable.COLUMN_STATUS};
    CursorLoader cursorLoader = new CursorLoader(this,
        MyTaskContentProvider.CONTENT_URI, projection, null, null, TaskTable.COLUMN_DESCRIPTION);
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
  
  public class CustomCursorAdapter extends CursorAdapter {
	  
	  private int layout;
	  
	  public CustomCursorAdapter (Context context, int layout, Cursor c, int flags) {
	        super(context, c, flags);
	        this.layout = layout;

	    }
	  @Override
	  public View newView(Context context, Cursor cursor, ViewGroup parent) {
		   final LayoutInflater inflater = LayoutInflater.from(context);
	        View v = inflater.inflate(layout, parent, false);
	        Cursor c = getCursor();
	 
	        
	 
	        return v;
	   
	  }
	  @Override
	  public void bindView(View view, Context context, Cursor cursor) {
	  
	   if(cursor.getPosition()%2==1) {
	    view.setBackgroundColor(context.getResources().getColor(R.color.white));
	   }
	   else {
	    view.setBackgroundColor(context.getResources().getColor(R.color.lightgrey));
	   }
	  
	   int descCol = cursor.getColumnIndex(TaskTable.COLUMN_DESCRIPTION);
       int nameCol1 = cursor.getColumnIndex(TaskTable.COLUMN_STATUS);
       String desc = cursor.getString(descCol);
       String name1 = cursor.getString(nameCol1);

       /**
        * Next set the name of the entry.
        */    
       TextView desc_text = (TextView) view.findViewById(R.id.label);
       TextView name_text1 = (TextView) view.findViewById(R.id.second_label);
       if (desc_text != null) {
           desc_text.setText(desc);
          name_text1.setText(name1);
       }
	  
	  }
	  
	 
	  
	 }

}

