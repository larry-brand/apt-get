package emerge.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import emerge.ebuild.EbuildFile;
import emerge.entity.WindowsRegistryEntry;
import emerge.exception.InternalException;
import emerge.misc.FileHelper;
import emerge.misc.Logger;
import emerge.misc.WinRegistry;
import emerge.repo.entity.InstalledPackageEntry;
import emerge.repo.entity.InstalledPackageFile;

public class InstalledPackagesHandler {
    
    private EbuildLocalRepositoryReader centralEbuildReader = new EbuildLocalRepositoryReader();
    
    public void init() throws InternalException {
	List<EbuildFile> infoFiles = centralEbuildReader.readAllCommonEbuilds();
	
	InstalledPackageFile file = new InstalledPackageFile();

	List<InstalledPackageEntry> installedEntries = new ArrayList<InstalledPackageEntry>();
	List<WindowsRegistryEntry> registryEntries = WinRegistry.getRegistryEntriesWindows();

	// find already installed
	initInstallPackageEntryWindows(infoFiles, registryEntries, installedEntries);
    }
    
    private void initInstallPackageEntryWindows(List<EbuildFile> ebuildFiles, List<WindowsRegistryEntry> registryEntries,
	    List<InstalledPackageEntry> entries) {
	List<WindowsRegistryEntry> registryEntryToRemove = new ArrayList<WindowsRegistryEntry>();
	// find with version
	for (WindowsRegistryEntry registryEntry : registryEntries) {
	    for (EbuildFile ebuild : ebuildFiles) {
		if (ebuild.getRegistryName() != null && registryEntry.getDisplayName() != null) {
		    Pattern p = Pattern.compile(ebuild.getRegistryName());
		    if (FileHelper.isFindByPattern(registryEntry.getDisplayName(), p)) {
			InstalledPackageEntry entry = new InstalledPackageEntry();
			entry.setPackageId(ebuild.getPackageId());
			entry.getPackageId().setVersion(registryEntry.getDisplayVersion());
			entry.setProductCode(registryEntry.getProductCode());
			entries.add(entry);
			registryEntryToRemove.add(registryEntry);
			// copy ebuild
			Logger.info("entry from registry added to install.db: " + entry + "------" + registryEntry.getDisplayName()
				+ ebuild.getRegistryName());
			break;
		    }
		}
	    }
	}
    }
    
}