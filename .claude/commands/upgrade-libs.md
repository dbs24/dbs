# Library upgrade commands

1. Verify new dependency project library versions, specified in toml file.  
2. If there are new versions, then install them and update toml file. Don't upgrade to new rc (release candidate), alfa, beta and milestone versions
3. Verify, that project is being successfully built ( ./sh/other/bc ). Fix the errors if there are any
4. Check the compatibility of new libraries with existing libraries
5. Verify all exists tests. Fix the errors if there are any
6. Repeat until you have checked all the updates from the list.
