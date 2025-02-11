//NOTE: these groovys are for MSSQL only, and need to be executed in commit mode in the HAC

// change column length on runtime

import de.hybris.platform.core.Registry;
conn = Registry.getCurrentTenant().getDataSource().getConnection();
stmt = conn.createStatement();
stmt.executeUpdate("ALTER TABLE [tablename] ALTER COLUMN [p_columnname] varchar(1000)")

// drop column
import de.hybris.platform.core.Registry;
conn = Registry.getCurrentTenant().getDataSource().getConnection();
stmt = conn.createStatement();
stmt.executeUpdate("ALTER TABLE [tablename] DROP COLUMN [p_columnname]")

// create a new index

import de.hybris.platform.core.Registry
conn = Registry.getCurrentTenant().getDataSource().getConnection()
stmt = conn.createStatement()
ddlQuery = "CREATE NONCLUSTERED INDEX [IDX_youridx_idxnumber] ON [dbo].[tablename] ([p_columnname]) with (online=on)"
stmt.executeUpdate(ddlQuery)