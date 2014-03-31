package com.todosapp;

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
  // private Cursor cursor;
  private SimpleCursorAdapter adapter;
  private CustomCursorAdapter newadapter;

  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.task_list);
    this.getListView().setDividerHeight(2);
    fillData();
    registerForContextMenu(getListView());
  }

  // Create the menu based on the XML definition
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.list_menu, menu);
    return true;
  }

  // Reaction to the menu selection
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.insert:
      createTask();
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

  private void fillData() {
    // Fields from the database (projection)
    // Must include the _id column for the adapter to work
    String[] from = new String[] { TaskTable.COLUMN_DESCRIPTION };
    // Fields on the UI to which we map
    int[] to = new int[] { R.id.label };

    getLoaderManager().initLoader(0, null, this);
    //adapter = new SimpleCursorAdapter(this, R.layout.task_row, null, from, to, 0);
    newadapter = new CustomCursorAdapter(getApplication(), R.layout.task_row, null, 0);
    //setListAdapter(new MyAdapter(this, android.R.layout.simple_list_item_1, R.id.label, from));
    setListAdapter(newadapter);
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
    String[] projection = { TaskTable.COLUMN_ID, TaskTable.COLUMN_DESCRIPTION };
    CursorLoader cursorLoader = new CursorLoader(this,
        MyTaskContentProvider.CONTENT_URI, projection, null, null, null);
    return cursorLoader;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    newadapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    // data is not available anymore, delete reference
    newadapter.swapCursor(null);
  }
  
  public class CustomCursorAdapter extends CursorAdapter {
	  
	  private Context context;
	   private int layout;
	  
	  public CustomCursorAdapter (Context context, int layout, Cursor c, int flags) {
	        super(context, c, flags);
	        this.context = context;
	        this.layout = layout;

	    }
	  @Override
	  public View newView(Context context, Cursor cursor, ViewGroup parent) {
		   final LayoutInflater inflater = LayoutInflater.from(context);
	        View v = inflater.inflate(R.layout.task_row, parent, false);
	        Cursor c = getCursor();
	 
	        int nameCol = c.getColumnIndex(TaskTable.COLUMN_DESCRIPTION);
	        //int nameCol1 = c.getColumnIndex(TaskTable.COLUMN_PRIORITY);
	 
	        String name = c.getString(nameCol);
	        //String name1 = c.getString(nameCol1);
	 
	        /**
	         * Next set the name of the entry.
	         */    
	        TextView name_text = (TextView) v.findViewById(R.id.label);
	        //TextView name_text1 = (TextView) v.findViewById(R.id.second_label);
	        if (name_text != null) {
	            name_text.setText(name);
	           // name_text1.setText(name1);
	        }
	 
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
	  
	   int nameCol = cursor.getColumnIndex(TaskTable.COLUMN_DESCRIPTION);
	   
       String name = cursor.getString(nameCol);

       /**
        * Next set the name of the entry.
        */    
       TextView name_text = (TextView) view.findViewById(R.id.label);
       if (name_text != null) {
           name_text.setText(name);
       }
	  
	  }
	  
	 
	  
	 }

}

