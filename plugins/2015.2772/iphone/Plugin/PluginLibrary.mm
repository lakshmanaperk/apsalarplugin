//
//  PluginLibrary.mm
//  TemplateApp
//
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "PluginLibrary.h"

#include "CoronaRuntime.h"

#import <UIKit/UIKit.h>


// ----------------------------------------------------------------------------

class PluginLibrary
{
	public:
		typedef PluginLibrary Self;

	public:
		static const char kName[];
		static const char kEvent[];

	protected:
		PluginLibrary();

	public:
		bool Initialize( CoronaLuaRef listener );

	public:
		CoronaLuaRef GetListener() const { return fListener; }

	public:
		static int Open( lua_State *L );

	protected:
		static int Finalizer( lua_State *L );

	public:
		static Self *ToLibrary( lua_State *L );

	public:
		static int init( lua_State *L );
        static int setEventListener( lua_State *L );
    
    
	private:
		CoronaLuaRef fListener;
        static void DispatchToLua(lua_State *L,Self *library);
    
};

// ----------------------------------------------------------------------------

// This corresponds to the name of the library, e.g. [Lua] require "plugin.library"
const char PluginLibrary::kName[] = "plugin.googleanalytics";
const char *PROVIDER_NAME = "coronaads";
const char *INIT = "init";
const char *START = "start";
const char *STARTFOUND = "found";
const char *STARTNOT_FOUND = "not_found";
const char *STARTLOADED = "load";
const char *CLOSED = "close";


//Lua Level Constant fields
const char *ADSREQUEST_TYPE = "googleAnalyticsRequest";
const char *ISERROR_KEY = "isError";
const char *PROVIDER_KEY = "provider";
const char *CONFIGURATION_ERROR = "configuration";
const char *ERRORTYPE_KEY = "errorType";

// This corresponds to the event name, e.g. [Lua] event.name
const char PluginLibrary::kEvent[] = "pluginlibraryevent";

PluginLibrary::PluginLibrary()
:	fListener( NULL )
{
}

bool
PluginLibrary::Initialize( CoronaLuaRef listener )
{
	// Can only initialize listener once
	bool result = ( NULL == fListener );

	if ( result )
	{
		fListener = listener;
	}

	return result;
}

int
PluginLibrary::Open( lua_State *L )
{
	// Register __gc callback
	const char kMetatableName[] = __FILE__; // Globally unique string to prevent collision
	CoronaLuaInitializeGCMetatable( L, kMetatableName, Finalizer );

	// Functions in library
	const luaL_Reg kVTable[] =
	{
		{ "init", init },
        {"setEventListener",setEventListener},
       
		{ NULL, NULL }
	};

	// Set library as upvalue for each library function
	Self *library = new Self;
	CoronaLuaPushUserdata( L, library, kMetatableName );

	luaL_openlib( L, kName, kVTable, 1 ); // leave "library" on top of stack

	return 1;
}

int
PluginLibrary::Finalizer( lua_State *L )
{
	Self *library = (Self *)CoronaLuaToUserdata( L, 1 );

	CoronaLuaDeleteRef( L, library->GetListener() );

	delete library;

	return 0;
}

PluginLibrary *
PluginLibrary::ToLibrary( lua_State *L )
{
	// library is pushed as part of the closure
	Self *library = (Self *)CoronaLuaToUserdata( L, lua_upvalueindex( 1 ) );
	return library;
}



int
PluginLibrary::setEventListener(lua_State *L){
    
    if ( CoronaLuaIsListener( L, 1, ADSREQUEST_TYPE ) )
    {
        Self *library = ToLibrary( L );
        CoronaLuaRef listener = CoronaLuaNewRef( L, 1 );
        library->Initialize( listener );
    }
    return 0;
}

void
PluginLibrary::DispatchToLua(lua_State *L,Self *library){
    
    // Self *library = ToLibrary( L ); //Some how not working here , need to figure out the problem .. meanwhile passing library.
    lua_pushstring( L, PROVIDER_NAME );
    lua_setfield( L, -2, PROVIDER_KEY);
    lua_pushstring( L, ADSREQUEST_TYPE );
    lua_setfield( L, -2, "name");
    CoronaLuaDispatchEvent( L, library->GetListener(), 0 );
}




// [Lua] library.init( listener )
int
PluginLibrary::init( lua_State *L )
{
  	return 0;
}



// ----------------------------------------------------------------------------

CORONA_EXPORT int luaopen_plugin_googleanalytics( lua_State *L )
{
	return PluginLibrary::Open( L );
}
