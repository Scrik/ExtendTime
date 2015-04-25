/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package extendtime;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin {

	private int extendratio = 1;
	
	private HashMap<String, Long> worldtime = new HashMap<String, Long>();
	
	public void onEnable()
	{
		//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doDaylightCycle false");
		for (World world : Bukkit.getWorlds()) {
			world.setGameRuleValue("doDaylightCycle", "false");
		}
		startTimeTask();
	}
	
	public void onDisable()
	{
		//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doDaylightCycle true");
		for (World world : Bukkit.getWorlds()) {
			world.setGameRuleValue("doDaylightCycle", "true");
		}
	}
	
	private BukkitTask timetask = null;
	private void startTimeTask()
	{
		File cfgfile = new File(this.getDataFolder(),"config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(cfgfile);
		extendratio = config.getInt("extendratio", extendratio);
		config.set("extendratio", extendratio);
		try {config.save(cfgfile);} catch (IOException e) {}
		
		if (timetask != null)
		{
			timetask.cancel();
		}
		timetask = Bukkit.getScheduler().runTaskTimer(this, new Runnable()
		{
			public void run()
			{
				for (World world : Bukkit.getWorlds())
				{
					String worldname = world.getName();
					if (!worldtime.containsKey(worldname))
					{
						worldtime.put(worldname, world.getTime());
					}
					if (worldtime.containsKey(worldname))
					{
						long wtime = worldtime.get(worldname)+1;
						if (wtime >= 24000) {wtime = 0;}
						worldtime.put(worldname, wtime);
						world.setTime(wtime);
					}
				}
			}
		}, 0, extendratio);
	}

}
