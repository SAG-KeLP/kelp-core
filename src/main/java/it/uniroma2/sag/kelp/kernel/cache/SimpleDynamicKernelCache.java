package it.uniroma2.sag.kelp.kernel.cache;

import com.fasterxml.jackson.annotation.JsonTypeName;

import gnu.trove.map.hash.TLongFloatHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.kernel.cache.KernelCache;
/**
 * Cache for kernel computations. It can stores all the pairwise kernel computations.
 * It is a simple, not optimized, implementation.
 * 
 * @author      Simone Filice
 */
@JsonTypeName("simpleDynamic")
public class SimpleDynamicKernelCache extends KernelCache {

	private TLongObjectHashMap<TLongFloatHashMap> map;

	public SimpleDynamicKernelCache() {
		map = new TLongObjectHashMap<TLongFloatHashMap>();
	}

	@Override
	protected Float getStoredKernelValue(Example exA, Example exB) {
		long idA = exA.getId();
		long idB = exB.getId();
		if (idA > idB) {
			idB = idA;
			idA = exB.getId();
		}

		if (!map.containsKey(idA))
			return null;

		TLongFloatHashMap tLongFloatHashMap = map.get(idA);
		if (tLongFloatHashMap.containsKey(idB))
			return tLongFloatHashMap.get(idB);
		return null;
	}

	@Override
	public void setKernelValue(Example exA, Example exB, float value) {

		long idA = exA.getId();
		long idB = exB.getId();
		if (idA > idB) {
			idB = idA;
			idA = exB.getId();
		}

		if (!map.contains(idA))
			map.put(idA, new TLongFloatHashMap());
		map.get(idA).put(idB, value);
	}

	@Override
	public void flushCache() {
		map.clear();
	}

}
