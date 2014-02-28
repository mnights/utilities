/**
 * The
 * <code>FileFilterWild</code> class filters file names. Class is capable of
 * filtering file names based on any file name passed in with, or without
 * wild-card chars in the search string. You can have any number of wild card
 * chars in any position. E.g. test* test.* test*.csv *.csv *.* test*.2012*
 * test*.*99
 * 
* @author Todd Steiger
 * @version 1.0.0
 * 
* _Modification History_______________________________ _Mod#_ ___Date___
 * _Programmer____________________ 001 05/30/2012 Todd Steiger
 * 
*/
package com.muggynights.utilities.file;

import java.io.*;

/**
 *
 * @author heather
 */
public class FileFilterWild implements FilenameFilter {

    static int codeLoc = 0;
//   private String path = null;
    private File startDir = null;
    private String likeFile = null;
    private String whereWilds = null;
    private int[] wilds;
    private int nbrOfWilds = 0;
    private boolean isBeginsWith = false;
    private boolean isEndsWith = false;

    /**
     * Constructor
     * @param likeFileName 
     */
    protected FileFilterWild(String likeFileName) {
        assert (likeFileName != null) : "Search string cannot be null!";
        this.likeFile = likeFileName;

        // Count number of wilds
        for (int index = 0; index < this.likeFile.length(); index++) {
            if (likeFile.charAt(index) == '*') {
                nbrOfWilds++;
            }
        }
        wilds = new int[nbrOfWilds];

        // Find the position of each wild
        int i = 0;
        int y = 0;
        for (i = 0, y = 0; i < this.likeFile.length(); i++) {
            if (likeFile.charAt(i) == '*') {
                wilds[y] = i;
                y++;

                if (i == 0) {
                    isBeginsWith = true;
                }
                if ((likeFile.length() - 1) == i) {
                    isEndsWith = true;
                }
            }
        }

        if (null != wilds) {
            nbrOfWilds = wilds.length;
        } else {
            nbrOfWilds = 0;
        }
    } // END constructor

    /**
     * Constructor
     */
    protected FileFilterWild() {
    };

    /**
     *
     * @param startDir
     */
    public void setStartDir(File startDir) {
        this.startDir = startDir;
    }
   
    /**
     * Instantiate FileFilterWild
     * @param likeFileName
     * @return
     */
    public static final FileFilterWild getFileFilterWild(String likeFileName) {
        assert (likeFileName != null) : "Search string cannot be null!";
        
        System.out.println("Search for \""+likeFileName+"\"");
        FileFilterWild ffw = new FileFilterWild(likeFileName);

        // Find the position last '/'
        int y = likeFileName.lastIndexOf("/", 0);
        
        File path = (y != -1)?new File(likeFileName.substring(0, y)):new File(".");
        System.out.println("path=" + path.getAbsolutePath());
        ffw.setStartDir(path);
        
        return ffw;
    }

    /**
     * Convenience method to return list of matching names.
     *
     * @return
     */
    public String[] getList() {
        return startDir.list(this);
    }

    /**
     * Convenience method to return list of matching names.
     *
     * @return
     */
    public File[] getFilesList() {
        return startDir.listFiles(this);
    }

    /**
     * Test file applying a wild-card filter.
     */
    public boolean accept(File dir, String name) {
        boolean isOk = false;

        codeLoc = 20;

        if (name.length() >= likeFile.length()) {
            // Conditions of acceptable files in list
            // Search string ends with *
            if ((nbrOfWilds > 0) && (isEndsWith && !isBeginsWith)) {
                // Ends with *, e.g. test*
                if ((nbrOfWilds == 1)
                        && likeFile.substring(0, wilds[0]).equals(name.substring(0, wilds[0]))) {
                    isOk = true;
                } // Ends with *, e.g. test*.2012*
                else if (nbrOfWilds > 1) {
                    int i = 0;
                    int y = 0;
                    int j = 0;
                    for (i = 0, j = 0; i < nbrOfWilds; i++) {
                        if (i == 0) {
                            j = 0;
                        } else {
                            j = wilds[i - 1];
                        }
                        if (i == 0
                                && likeFile.substring(j, wilds[i]).equals(name.substring(j, wilds[i]))) {
                            y++;
                        } else if (i > 0
                                && (name.indexOf(likeFile.substring(j + 1, wilds[i])) != -1)) {
                            y++;
                        } else {
                            break;
                        }
                    } // END for

                    if (y != 0
                            && (y % nbrOfWilds) == 0) {
                        isOk = true;
                    }
                } // END else if

            } // Begins with *, e.g. *.csv
            else if ((nbrOfWilds > 0) && (isBeginsWith && !isEndsWith)) {
                codeLoc = 25;
                // Begins with *, e.g. *.csv
                if ((nbrOfWilds == 1)
                        && name.endsWith(likeFile.substring(1))) {
                    isOk = true;
                } else if (nbrOfWilds > 1) {
                    int i = 0;
                    int y = 0;
                    int j = 1;
                    int foundIndex = 0;
                    int foundLength = 0;
                    for (i = 0, j = 1; i < nbrOfWilds; i++) {
                        if (i == 0) {
                            j = 1;
                        } else {
                            j = wilds[i - 1];
                        }

                        if (i == 0) {
                            if (name.indexOf(likeFile.substring(j, wilds[i + 1])) != -1) {
                                foundIndex = name.indexOf(likeFile.substring(j, wilds[i + 1]));
                                foundLength = likeFile.substring(j, wilds[i + 1]).length();
                                y++;
                            }
                        } else if (i > 0) {
                            if (name.indexOf(likeFile.substring(wilds[i] + 1), foundIndex + foundLength) != -1) {
                                y++;
                            }
                        } else {
                            break;
                        }
                    } // END for

                    if (y != 0
                            && (y % nbrOfWilds) == 0) {
                        isOk = true;
                    }
                }
            } // Substring to *, e.g. test*.*99
            else if ((nbrOfWilds > 0) && (!isEndsWith && !isBeginsWith)) {
                codeLoc = 30;

                int i = 0;
                int y = 0;
                int j = 0;
                int foundIndex = 0;
                int foundLength = 0;
                for (i = 0, j = 0; i < nbrOfWilds; i++) {
                    if (i == 0) {
                        j = 0;
                    } else {
                        j = wilds[i - 1];
                    }

                    if (i == 0) {
                        if (name.startsWith(likeFile.substring(j, wilds[i]))) {
                            foundIndex = name.indexOf(name.substring(j, wilds[i]));
                            foundLength = name.substring(j, wilds[i]).length();
                            y++;
                        }
                    } else if (i > 0) {
                        if (name.indexOf(likeFile.substring(wilds[i - 1] + 1, wilds[i]), foundIndex + foundLength) != -1) {
                            foundIndex = name.indexOf(likeFile.substring(wilds[i - 1] + 1, wilds[i]), foundIndex + foundLength);
                            foundLength = likeFile.substring(wilds[i - 1] + 1, wilds[i]).length();
                            y++;
                        }
                    } else {
                        break;
                    }
                }
                if (name.indexOf(likeFile.substring(wilds[i - 1] + 1), foundIndex + foundLength) != -1) {
                    y++;
                }

                if (y != 0
                        && (y % (nbrOfWilds + 1)) == 0) {
                    isOk = true;
                }
            } // Only one acceptable
            else if ((nbrOfWilds == 0) && name.equals(likeFile)) {
                isOk = true;
            } // All acceptable
            else if (likeFile.equals("*.*") || likeFile.equals("*")) {
                isOk = true;
            }
        } // END if (name.length() >= likeFile.length())

        return (isOk);
    }

    /**
     * main
     *
     * main really used for testing
     * @param args 
     */
    public static void main(String args[]) {
        int rtnCode = 0;
        String pathFileName = args[0];
        System.out.println("args="+pathFileName);
        
        try {
            String[] fileList = FileFilterWild.getFileFilterWild(pathFileName).getList();

            System.out.println("List Files in dir:");

            for (int i = 0; i < fileList.length; i++) {
                System.out.println(fileList[i]);
            }
        } catch (Exception e) {
            rtnCode = 99;
            e.printStackTrace();
        }

        System.exit(rtnCode);
    }

    
} // END FileFilterWild