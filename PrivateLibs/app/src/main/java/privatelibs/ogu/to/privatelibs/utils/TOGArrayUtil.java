package privatelibs.ogu.to.privatelibs.utils;

import java.util.ArrayList;

public class TOGArrayUtil {

	public static String join( String glue, ArrayList<String> pieces ) {
		if ( pieces == null ) {
			return null;
		}
		if ( pieces.size() == 0 ) {
			return "";
		}
		return TOGArrayUtil.join( glue, (String[]) pieces.toArray( new String[ 0 ] ) );
	}

	public static String join( String glue, String[] pieces ) {
		if ( glue == null ) {
			glue = "";
		}
		if ( pieces == null ) {
			return null;
		}
		if ( pieces.length == 0 ) {
			return "";
		}
		String join = "";
		for ( int i = 0; i < pieces.length - 1; i++ ) {
			join = join.concat( pieces[ i ].concat( glue ) );
		}
		join = join.concat( pieces[ pieces.length - 1 ] );
		return join;
	}

	public static final int NOT_FOUND = -1;

}
