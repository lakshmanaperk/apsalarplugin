//
//  LuaLoader.java
//  TemplateApp
//
//  Copyright (c) 2015 Perk.com Inc. All rights reserved.
//

// This corresponds to the name of the Lua library,
// e.g. [Lua] require "plugin.apsalar"
package plugin.apsalar;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.naef.jnlua.LuaException;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.LuaType;
import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.NamedJavaFunction;

import com.ansca.corona.CoronaActivity;
import com.ansca.corona.CoronaEnvironment;
import com.ansca.corona.CoronaLua;
import com.ansca.corona.CoronaRuntime;
import com.ansca.corona.CoronaRuntimeListener;
import com.ansca.corona.CoronaRuntimeTask;
import com.ansca.corona.CoronaRuntimeTaskDispatcher;

/**
 * Implements the Lua interface for a Corona plugin.
 * <p>
 * Only one instance of this class will be created by Corona for the lifetime of the application.
 * This instance will be re-used for every new Corona activity that gets created.
 */
public class LuaLoader implements JavaFunction, CoronaRuntimeListener {

    private CoronaRuntimeTaskDispatcher taskDispatcher;

    private String API_KEY = "";

    private String API_SECRET ="";

	private LuaState luaState = null;

	/**
	 * Creates a new Lua interface to this plugin.
	 * <p>
	 * Note that a new LuaLoader instance will not be created for every CoronaActivity instance.
	 * That is, only one instance of this class will be created for the lifetime of the application process.
	 * This gives a plugin the option to do operations in the background while the CoronaActivity is destroyed.
	 */
	public LuaLoader() {
		// Set up this plugin to listen for Corona runtime events to be received by methods
		// onLoaded(), onStarted(), onSuspended(), onResumed(), and onExiting().
		CoronaEnvironment.addRuntimeListener(this);
	}

    
	


  /**
     * Functions for [Lua: CoronaApsalar.init(API_KEY, API_SECRET)
     */
    private int invokeInit(LuaState L) {
        luaState = L;
        CoronaActivity coronaActivity = CoronaEnvironment.getCoronaActivity();
        
        //check if activity is not null
        if ( coronaActivity!= null ){
            coronaActivity = CoronaEnvironment.getCoronaActivity();
        	//check if first arguement is string type else throw error 
	        if (LuaType.STRING == luaState.type(1)) {
	            API_KEY = luaState.toString(1);
	        }
	        else
	        {
	        	throw new IllegalArgumentException("ERROR:plugin.apsalar.init(apikey, app secret): apikey expected, got " + luaState.type(1));
	        }
        	//check if second arguement is string type else throw error 
	        if (LuaType.STRING == luaState.type(2)) {
	            API_SECRET = luaState.toString(2);
	        }
	        else
	        {
	        	throw new IllegalArgumentException("ERROR:plugin.apsalar.init(apikey, app secret): app secret expected, got " + luaState.type(2));
	        }
        	//check if both arguemtnts  are valid  else throw error 
	        
	        if(API_KEY.length() >0 && API_SECRET.length() > 0 )
	        {
	        	CoronaApsalar.init(coronaActivity, API_KEY,API_SECRET);
	        }
	        else
	        {
	        	throw new IllegalArgumentException("ERROR:plugin.apsalar.init(apikey, app secret): api key and app secret must not be empty, got " + luaState.type(1) + "  and " +luaState.type(2));
	        }
	    }
        return 0;
    }

    /**
     * Functions for [Lua: CoronaApsalar.logEvent(Event)
     */
    private int invokeLogEvent(LuaState L) {
        luaState = L;

        String event = "";
        //check if first arguement is string type else throw error 
        if (LuaType.STRING == luaState.type(1)) {
            event = luaState.toString(1);
        }
        else
        {
        	throw new IllegalArgumentException("ERROR:plugin.apsalar.logEvent(event): event expected, got " + luaState.type(1));
        }
        //check if  arguemtnts  is valid  else throw error 
        if(event.length() > 0)
        {
        	CoronaApsalar.sendevent(event);
        }
        else
        {
        	throw new IllegalArgumentException("ERROR:plugin.apsalar.logEvent(event): event and must not be empty, got " + luaState.type(1));
        }
        return 0;
    }




	/**
     * Initialise Apsalar SDK . [Lua: CoronaApsalar.init(APPKEY,APPSECRET)]
     */
    private class InitWrapper implements NamedJavaFunction {

    	//lua name for the function
        public String getName() {
            return "init";
        }
        // calls respective API
        public int invoke(LuaState L) {
            return invokeInit(L);
        }
    }
	/**
     * LogEvent CoronaApsalar. [Lua: CoronaApsalar.LogEvent(event)]
     */
    private class LogEventWrapper implements NamedJavaFunction {

		//lua name for the function
        public String getName() {
            return "logEvent";
        }
        // calls respective API
        public int invoke(LuaState L) {
            return invokeLogEvent(L);
        }
    }



	/**
	 * Called when this plugin is being loaded via the Lua require() function.
	 * <p>
	 * Note that this method will be called everytime a new CoronaActivity has been launched.
	 * This means that you'll need to re-initialize this plugin here.
	 * <p>
	 * Warning! This method is not called on the main UI thread.
	 * @param L Reference to the Lua state that the require() function was called from.
	 * @return Returns the number of values that the require() function will return.
	 *         <p>
	 *         Expected to return 1, the library that the require() function is loading.
	 */
	@Override
	public int invoke(LuaState L) {
		// Register this plugin into Lua with the following functions.
		NamedJavaFunction[] luaFunctions = new NamedJavaFunction[] {
            new InitWrapper(),
            new LogEventWrapper(),
		};
		String libName = L.toString( 1 );
		L.register(libName, luaFunctions);

		// Returning 1 indicates that the Lua require() function will return the above Lua library.
		return 1;
	}

	/**
	 * Called after the Corona runtime has been created and just before executing the "main.lua" file.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that has just been loaded/initialized.
	 *                Provides a LuaState object that allows the application to extend the Lua API.
	 */
	@Override
	public void onLoaded(CoronaRuntime runtime) {
		// Note that this method will not be called the first time a Corona activity has been launched.
		// This is because this listener cannot be added to the CoronaEnvironment until after
		// this plugin has been required-in by Lua, which occurs after the onLoaded() event.
		// However, this method will be called when a 2nd Corona activity has been created.
        taskDispatcher = new CoronaRuntimeTaskDispatcher(runtime.getLuaState());
	}

	/**
	 * Called just after the Corona runtime has executed the "main.lua" file.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that has just been started.
	 */
	@Override
	public void onStarted(CoronaRuntime runtime) {
	}

	/**
	 * Called just after the Corona runtime has been suspended which pauses all rendering, audio, timers,
	 * and other Corona related operations. This can happen when another Android activity (ie: window) has
	 * been displayed, when the screen has been powered off, or when the screen lock is shown.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that has just been suspended.
	 */
	@Override
	public void onSuspended(CoronaRuntime runtime) {
	}

	/**
	 * Called just after the Corona runtime has been resumed after a suspend.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that has just been resumed.
	 */
	@Override
	public void onResumed(CoronaRuntime runtime) {
	}

	/**
	 * Called just before the Corona runtime terminates.
	 * <p>
	 * This happens when the Corona activity is being destroyed which happens when the user presses the Back button
	 * on the activity, when the native.requestExit() method is called in Lua, or when the activity's finish()
	 * method is called. This does not mean that the application is exiting.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that is being terminated.
	 */
	@Override
	public void onExiting(CoronaRuntime runtime) {
		// Remove the Lua listener reference.
    }
}
