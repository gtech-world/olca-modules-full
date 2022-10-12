package org.openlca.ipc.handlers;

import java.util.ArrayList;

import org.openlca.core.matrix.index.EnviFlow;
import org.openlca.core.matrix.index.TechFlow;
import org.openlca.core.model.descriptors.ImpactDescriptor;
import org.openlca.core.results.Contribution;
import org.openlca.core.results.LocationResult;
import org.openlca.ipc.Responses;
import org.openlca.ipc.Rpc;
import org.openlca.ipc.RpcRequest;
import org.openlca.ipc.RpcResponse;
import org.openlca.jsonld.Json;

import com.google.gson.JsonArray;

public class ImpactHandler {

	private final HandlerContext context;

	public ImpactHandler(HandlerContext context) {
		this.context = context;
	}

	@Rpc("get/impacts")
	public RpcResponse getImpacts(RpcRequest req) {
		return ResultRequest.of(req, context, rr -> {
			var result = rr.result();
			if (!result.hasImpacts())
				return Responses.ok(new JsonArray(), req);
			var array = result.totalImpacts().stream()
					.filter(r -> r.value() != 0)
					.map(r -> JsonRpc.encodeImpactValue(r, rr.refs()))
					.collect(JsonRpc.toArray());
			return Responses.ok(array, req);
		});
	}

	@Rpc("get/impacts/contributions/flows")
	public RpcResponse getFlowContributions(RpcRequest req) {
		return ResultRequest.of(req, context, rr -> {
			if (rr.impact() == null)
				return rr.impactMissing();
			var contributions = new ArrayList<Contribution<EnviFlow>>();
			double total = rr.result().totalImpactOf(rr.impact());
			for (var enviFlow : rr.result().enviIndex()) {
				var amount = rr.result().flowImpactOf(rr.impact(), enviFlow);
				if (amount == 0)
					continue;
				var c = new Contribution<EnviFlow>();
				c.item = enviFlow;
				c.amount = amount;
				c.computeShare(total);
				c.unit = rr.impact().referenceUnit;
				contributions.add(c);
			}
			var array = JsonRpc.encodeContributions(
					contributions,
					f -> JsonRpc.encodeEnviFlow(f, rr.refs()));
			return Responses.ok(array, req);
		});
	}

	@Rpc("get/impacts/contributions/process/flows")
	public RpcResponse getFlowContributionsForProcess(RpcRequest req) {
		return ResultRequest.of(req, context, rr -> {
			var techFlow = rr.techFlow();
			if (techFlow == null)
				return rr.providerMissing();
			var impact = rr.impact();
			if (impact == null)
				return rr.impactMissing();

			var result = rr.result();
			double total = result.directImpactOf(techFlow, impact);
			var contributions = new ArrayList<Contribution<EnviFlow>>();
			for (var enviFlow : result.enviIndex()) {
				var factor = result.impactFactorOf(impact, enviFlow);
				var flowAmount = result.getDirectFlowResult(techFlow, enviFlow);
				var amount = factor * flowAmount;
				if (amount == 0)
					continue;
				var c = new Contribution<EnviFlow>();
				c.item = enviFlow;
				c.amount = amount;
				c.computeShare(total);
				c.unit = impact.referenceUnit;
				contributions.add(c);
			}
			var array = JsonRpc.encodeContributions(
					contributions,
					enviFlow -> JsonRpc.encodeEnviFlow(enviFlow, rr.refs()));
			return Responses.ok(array, req);
		});
	}

	@Rpc("get/impacts/contributions/processes")
	public RpcResponse getProcessContributions(RpcRequest req) {
		return ResultRequest.of(req, context, rr -> {
			if (rr.impact() == null)
				return rr.impactMissing();
			var result = rr.result();
			double total = result.totalImpactOf(rr.impact());
			var contributions = new ArrayList<Contribution<TechFlow>>();
			for (var techFlow : result.techIndex()) {
				var amount = result.directImpactOf(techFlow, rr.impact());
				if (amount == 0)
					continue;
				var c = new Contribution<TechFlow>();
				c.item = techFlow;
				c.amount = amount;
				c.computeShare(total);
				c.unit = rr.impact().referenceUnit;
				contributions.add(c);
			}
			var array = JsonRpc.encodeContributions(
					contributions,
					techFlow -> JsonRpc.encodeTechFlow(techFlow, rr.refs()));
			return Responses.ok(array, req);
		});
	}

	@Rpc("get/impacts/contributions/locations")
	public RpcResponse getLocationContributions(RpcRequest req) {
		return ResultRequest.of(req, context, rr -> {
			var result = rr.result();
			var impact = rr.impact();
			if (impact == null)
				return rr.impactMissing();
			var r = new LocationResult(result, context.db());
			var array = r.getContributions(impact).stream()
					.filter(c -> c.amount != 0)
					.map(c -> JsonRpc.encodeContribution(c, Json::asRef))
					.collect(JsonRpc.toArray());
			return Responses.ok(array, req);
		});
	}

	@Rpc("get/impacts/process_results")
	public RpcResponse getProcessResultsImpacts(RpcRequest req) {
		return ResultRequest.of(req, context, rr -> {
			var result = rr.result();
			var techFlow = rr.techFlow();
			if (techFlow == null)
				return rr.providerMissing();

			var array = new JsonArray();
			if (!result.hasImpacts())
				return Responses.ok(array, req);

			for (var impact : result.impactIndex()) {
				var total = result.totalImpactOf(impact);
				var c = new Contribution<ImpactDescriptor>();
				c.item = impact;
				c.amount = result.directImpactOf(techFlow, impact);
				c.computeShare(total);
				c.unit = impact.referenceUnit;
				var obj = JsonRpc.encodeContribution(c, rr.refs()::asRef);
				Json.put(obj, "upstream",
						result.getUpstreamImpactResult(techFlow, impact));
				array.add(obj);
			}
			return Responses.ok(array, req);
		});
	}

	@Rpc("get/impacts/upstream")
	public RpcResponse getUpstream(RpcRequest req) {
		return ResultRequest.of(req, context, rr -> {
			if (rr.impact() == null)
				return rr.impactMissing();
			var tree = rr.result().getTree(rr.impact());
			return Upstream.getNodesForPath(rr, tree);
		});
	}
}
